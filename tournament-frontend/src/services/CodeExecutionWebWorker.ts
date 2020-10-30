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

export function codeExecutionWorker(code: string, functions: CodeExecutionDescription[]): Promise<CodeExecutionResult[]> {
    return new Promise((resolve: (value: CodeExecutionResult[]) => void, reject) => {
        const workerJavascript = `${code}
        var logFn = console.log;
        self.addEventListener('message', function(event) {
            self.postMessage(
                event.data.functions.map(test => {
                    var log = [];
        
                    console.log = (...args) => {
                        log.push(args.map(e => JSON.stringify(e)).join(', '));
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

        const blobUrl = URL.createObjectURL(new Blob([workerJavascript], { type: 'text/javascript' }));

        const worker = new Worker(blobUrl);

        const timeout = setTimeout(() => {
            worker.terminate();
            resolve([{
                description: 'Timeout: Your code is too slow and timed out after ' + TIMEOUT + ' ms',
                fn: '',
                executionTime: TIMEOUT,
                error: 'Your code is too slow and timed out after ' + TIMEOUT + ' ms'
            }]);
        }, TIMEOUT);

        const kill = () => {
            clearTimeout(timeout);
            worker.terminate();
            URL.revokeObjectURL(blobUrl);
        }

        worker.onmessage = (event) => {
            kill();
            resolve(event.data as CodeExecutionResult[]);
        }
        worker.onerror = (error) => {
            kill();
            if (error.message !== undefined) {
                reject(error);
            }
        }
        worker.postMessage({
            functions: functions
        });
    });
}