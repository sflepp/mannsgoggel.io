const TIMEOUT = 1000;

export interface CodeExecutionDescription {
    description: string,
    fn: string
}

export interface CodeExecutionResult {
    description: string,
    fn: string,
    executionTime: number,
    consoleOutput?: ConsoleLog[],
    result?: string,
    error?: string
}

export interface ConsoleLog {
    level: 'log' | 'error';
    payload: any[];
}

interface WorkerHolder { url: string, worker: Worker };

const cache = new Map<number, WorkerHolder>();

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

export function codeExecutionWorker(code: string, execution: CodeExecutionDescription, useCache: boolean = false): Promise<CodeExecutionResult> {
    return new Promise((resolve: (value: CodeExecutionResult) => void) => {
        const workerJavascript = `${code}
        var logFn = console.log;
        console.clear();
        
        self.addEventListener('message', function(event) {
            var log = [];
        
            console.log = (...args) => {
                log.push({level: 'log', payload: args });
                logFn('worker', ...args);
            }
        
            var t0 = performance.now()
            var result = eval(event.data.fn);
            var t1 = performance.now()
        
            let execution;
            try {
                execution = { description: event.data.description, fn: event.data.fn, result: JSON.stringify(result), consoleOutput: log, executionTime: parseInt(t1 - t0) }
            } catch (e) {
                var t1 = performance.now()
                execution = { description: event.data.description, fn: event.data.fn, error: JSON.stringify(e), consoleOutput: log, executionTime: parseInt(t1 - t0) }
            }
        
            self.postMessage(execution);
        }, false);`;

        let workerHolder: WorkerHolder;
        let hash: number;

        if (useCache) {
            const hash = hashCode(workerJavascript);

            if (!cache.has(hash)) {
                cache.set(hash, createWorker(workerJavascript));
            }

            workerHolder = cache.get(hash);
        } else {
            workerHolder = createWorker(workerJavascript);
        }

        const timeout = setTimeout(() => {
            forceKill();
            resolve({
                ...execution,
                ...{
                    value: null,
                    error: JSON.stringify({message: 'Your code is too slow and timed out after ' + TIMEOUT + ' ms'}),
                    fn: code,
                    executionTime: TIMEOUT
                }
            });
        }, TIMEOUT);

        const kill = () => {
            clearTimeout(timeout);

            if (!useCache) {
                workerHolder.worker.terminate();
                URL.revokeObjectURL(workerHolder.url);
            }
        }

        const forceKill = () => {
            clearTimeout(timeout);
            cache.delete(hash);
            workerHolder.worker.terminate();
            URL.revokeObjectURL(workerHolder.url);
        }

        workerHolder.worker.onmessage = (event) => {
            kill();
            resolve(event.data);
        }

        workerHolder.worker.onerror = (error) => {
            kill();
            if (error.message !== undefined) {
                resolve({
                    ...execution,
                    ...{
                        value: null,
                        error: error.message,
                        fn: code,
                        executionTime: 0
                    }
                });
            }
        }
        workerHolder.worker.postMessage(execution);
    });
}