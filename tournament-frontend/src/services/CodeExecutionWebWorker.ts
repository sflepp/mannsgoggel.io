import { transformSync } from "@babel/core";

const TIMEOUT = 1000;

export interface CodeExecutionRequest {
  description: string,
  code: string,
  compiledCode?: string,
  action: 'DECIDE_SHIFT' | 'SET_PLAYING_MODE' | 'PLAY_CARD';
  parameters: any;
}

export interface CodeExecutionResult {
  request: CodeExecutionRequest,
  executionTime: number,
  consoleOutput: ConsoleLog[],
  result: string,
  error: string
}

export interface ConsoleLog {
  level: 'log' | 'error';
  payload: any[];
}

export function codeExecutionWorker(request: CodeExecutionRequest): Promise<CodeExecutionResult> {
  return new Promise((resolve: (value: CodeExecutionResult) => void) => {

    let transpiled;

    try {
      transpiled = transformSync(request.code, { presets: [[require('@babel/preset-typescript'), { allExtensions: true }]] });
    } catch (e) {
      return resolve({
        request: request,
        executionTime: 0,
        consoleOutput: [],
        result: null,
        error: JSON.stringify({ message: 'Your code did not transpile.' }),
      });
    }

    const compiledRequest = {
      ...request,
      ...{ compiledCode: transpiled.code }
    }

    const worker = new Worker('jass-worker/worker.js');

    const timeout = setTimeout(() => {
      kill();

      resolve({
        request: compiledRequest,
        executionTime: TIMEOUT,
        consoleOutput: [],
        result: null,
        error: JSON.stringify({ message: 'Your code is too slow and timed out after ' + TIMEOUT + ' ms' }),
      });
    }, TIMEOUT);

    const kill = () => {
      clearTimeout(timeout);
      worker.terminate();
    }

    worker.onmessage = (event) => {
      kill();
      resolve({
        ...event.data,
        ...{ request: compiledRequest }
      });
    }

    worker.postMessage(compiledRequest);
  });
}