import { ConsoleLog } from "../../services/CodeExecutionWebWorker";
import React from "react";
import { JSONObject } from "../ui/JSONObject";
import { Divider } from "antd";

export const ConsoleView = (props: { logs: ConsoleLog[] }) => {

    const logOutput = props.logs.map((log: ConsoleLog) => {
        const payload = log.payload;

        if (payload.length === 2 && typeof payload[0] === 'string' && typeof payload[1] === 'object') {
            return <JSONObject name={payload[0]} object={payload[1]} collapsed={2}/>
        }

        return payload.map((log, i) => {
            switch (typeof log) {
                case "object":
                    return <span key={i}><JSONObject name={null} object={log} collapsed={2}/>&nbsp;</span>
                default:
                    return <span key={i} style={{ fontFamily: 'monospace' }}>{log}&nbsp;</span>;
            }
        });
    });

    if (logOutput.length === 0) {
        return <div>-</div>;
    }

    if(logOutput.length > 100) {
        return <div>Too many logs.</div>
    }

    return <div>{logOutput.map((logOutputEntry, i) => <div key={i}>{logOutputEntry}<Divider/></div>)}</div>;
}