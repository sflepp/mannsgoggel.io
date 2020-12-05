import { transformSync } from "@babel/core";

const TIMEOUT = 1000;

export interface CodeExecutionDescription {
  description: string,
  code: string,
  action: string;
  parameters: any;
}

export interface CodeExecutionResult {
  description: string,
  executionTime: number,
  consoleOutput?: ConsoleLog[],
  action: string,
  code: string,
  result?: string,
  error?: string
}

export interface ConsoleLog {
  level: 'log' | 'error';
  payload: any[];
}

export function codeExecutionWorker(execution: CodeExecutionDescription): Promise<CodeExecutionResult> {
  return new Promise((resolve: (value: CodeExecutionResult) => void) => {

    let transpiled;

    try {
      transpiled = transformSync(execution.code, { presets: [[require('@babel/preset-typescript'), { allExtensions: true }]] });
    } catch (e) {
      return resolve({
        ...execution,
        ...{
          value: null,
          error: JSON.stringify({ message: 'Your code did not transpile.' }),
          executionTime: 0,
          code: execution.code
        }
      });
    }

    const compiledExecution = {
      ...execution,
      ...{ code: transpiled.code }
    }

    const worker = new Worker('jass-worker/worker.js');

    const timeout = setTimeout(() => {
      forceKill();
      resolve({
        ...execution,
        ...{
          value: null,
          error: JSON.stringify({ message: 'Your code is too slow and timed out after ' + TIMEOUT + ' ms' }),
          executionTime: TIMEOUT,
          code: execution.code
        }
      });
    }, TIMEOUT);

    const kill = () => {
      clearTimeout(timeout);
      worker.terminate();
    }

    const forceKill = () => {
      clearTimeout(timeout);
      worker.terminate();
    }

    worker.onmessage = (event) => {
      kill();
      resolve({
        ...event.data,
          ...{code: execution.code}
      });
    }

    worker.onerror = (error) => {
      kill();
      if (error.message !== undefined) {
        resolve({
          ...execution,
          ...{
            value: null,
            error: error.message,
            executionTime: 0,
            code: execution.code
          }
        });
      }
    }
    worker.postMessage(compiledExecution);
  });
}