import { CodeRunResult } from '../reducers';

export function testWorker(code: string, functions: { description: string, fn: string }[]): { run: () => Promise<any>, instance: Worker } {
    const workerJavascript = `${code}
        self.addEventListener('message', function(event) {
            self.postMessage(
                event.data.functions.map(test => {
                    try {
                        return { description: test.description, fn: test.fn, result: JSON.stringify(eval(test.fn)) }
                    } catch (e) {
                        return { description: test.description, fn: test.fn, error: JSON.stringify(e) }
                    }
                })
            );
        }, false);`;


    const worker = new Worker(URL.createObjectURL(
        new Blob([workerJavascript], {
            type: 'text/javascript'
        }))
    );

    return {
        instance: worker,
        run: () => new Promise((resolve: (value: CodeRunResult[]) => void, reject) => {

            const timeout = setTimeout(() => {
                resolve([{
                    description: 'Timeout',
                    fn: '',
                    error: 'Your code is too slow and timed out after 1000 ms'
                }]);
                worker.terminate();
            }, 1000);

            worker.onmessage = (event) => {
                clearTimeout(timeout);
                resolve(<CodeRunResult[]>event.data);
            }
            worker.onerror = (error) => {
                clearTimeout(timeout);
                if (error.message !== undefined) {
                    reject(error);
                }
            }
            worker.postMessage({
                functions: functions
            });
        })
    }
}