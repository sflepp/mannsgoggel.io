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
        self.addEventListener('message', function(event) {
            self.postMessage(
                event.data.functions.map(test => {
                    var log = [];
                    var logFn = console.log;
        
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

        const worker = new Worker(URL.createObjectURL(
            new Blob([workerJavascript], {
                type: 'text/javascript'
            }))
        );

        const timeout = setTimeout(() => {
            worker.terminate();
            resolve([{
                description: 'Timeout: Your code is too slow and timed out after ' + TIMEOUT + ' ms',
                fn: '',
                executionTime: TIMEOUT,
                error: 'Your code is too slow and timed out after ' + TIMEOUT + ' ms'
            }]);
        }, TIMEOUT);

        worker.onmessage = (event) => {
            clearTimeout(timeout);
            worker.terminate();
            resolve(event.data as CodeExecutionResult[]);
        }
        worker.onerror = (error) => {
            clearTimeout(timeout);
            worker.terminate();
            if (error.message !== undefined) {
                reject(error);
            }
        }
        worker.postMessage({
            functions: functions
        });
    });
}