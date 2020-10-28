export async function runWebWorker(code: string, functions: string[]) {
    return new Promise((resolve: (value?: any[]) => void, reject) => {
        const workerJavascript = `${code}
        self.addEventListener('message', function(event) {
            const result = event.data.functions.map(fn => eval(fn)); 
            self.postMessage(result);
        }, false);`;

        try {

            console.log('asdf');

            const worker = new Worker(URL.createObjectURL(
                new Blob([workerJavascript], {
                    type: 'text/javascript'
                }))
            );

            worker.onmessage = (event) => {
                console.log('ww message', event);
                resolve(event.data);
            }

            worker.onerror = (error) => {
                console.error('webworker error', error);

                if (error.message !== undefined) {
                    reject(error);
                }
            }
            worker.postMessage({
                functions: functions
            })
        } catch (e) {
            console.error(e);
        }
    });
}