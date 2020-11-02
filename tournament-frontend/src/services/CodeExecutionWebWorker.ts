const TIMEOUT = 1000;

export interface CodeExecutionDescription {
    description: string,
    fn: string
}

export interface CodeExecutionResult {
    description: string,
    fn: string,
    executionTime: number,
    consoleOutput?: string[],
    result?: string,
    error?: string
}

interface WorkerHolder { url: string, worker: Worker };

const cache: WorkerHolder[] = [];

const hashCode = (s: string) => {
    return s.split("").reduce((a, b) => {
        a = ((a << 5) - a) + b.charCodeAt(0);
        return a & a
    }, 0);
}

const createWorker = (code: string): WorkerHolder => {
    const blobUrl = URL.createObjectURL(new Blob([code], { type: 'text/javascript' }))
    return { url: blobUrl, worker: new Worker(blobUrl) };
}

export function codeExecutionWorker(code: string, functions: CodeExecutionDescription[], useCache: boolean = false): Promise<CodeExecutionResult[]> {
    return new Promise((resolve: (value: CodeExecutionResult[]) => void, reject) => {
        const workerJavascript = `${code}
        var logFn = console.log;
        self.addEventListener('message', function(event) {
            self.postMessage(
                event.data.functions.map(test => {
                    var log = [];
        
                    console.log = (...args) => {
                        log.push(JSON.stringify(args));
                        logFn('worker', ...args);
                    }
                    
                    var t0 = performance.now()
                    var result = eval(test.fn);
                    var t1 = performance.now()
                    
                    try {
                        return { description: test.description, fn: test.fn, result: JSON.stringify(result), consoleOutput: log, executionTime: parseInt(t1 - t0) }
                    } catch (e) {
                        var t1 = performance.now()
                        return { description: test.description, fn: test.fn, error: JSON.stringify(e), consoleOutput: log, executionTime: parseInt(t1 - t0) }
                    }
                })
            );
        }, false);`;

        let workerHolder: WorkerHolder;

        if (useCache) {
            const hash = hashCode(workerJavascript);

            if (cache[hash] === undefined) {
                cache[hash] = createWorker(workerJavascript);
            }

            workerHolder = cache[hash];
        } else {
            workerHolder = createWorker(workerJavascript);
        }

        const timeout = setTimeout(() => {
            kill();
            resolve([{
                description: 'Timeout: Your code is too slow and timed out after ' + TIMEOUT + ' ms',
                fn: '',
                executionTime: TIMEOUT,
                error: 'Your code is too slow and timed out after ' + TIMEOUT + ' ms'
            }]);
        }, TIMEOUT);

        const kill = () => {
            if (!useCache) {
                clearTimeout(timeout);
                workerHolder.worker.terminate();
                URL.revokeObjectURL(workerHolder.url);
            }
        }

        workerHolder.worker.onmessage = (event) => {
            kill();
            resolve(event.data as CodeExecutionResult[]);
        }
        workerHolder.worker.onerror = (error) => {
            kill();
            if (error.message !== undefined) {
                reject(error);
            }
        }
        workerHolder.worker.postMessage({
            functions: functions
        });
    });
}