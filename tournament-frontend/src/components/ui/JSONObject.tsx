import React from "react";

const ReactJson = require("react-json-view");


export const JSONObject = (props: { name: string, object: any, collapsed?: number }) => {
    return <div>
        <ReactJson
            theme={'grayscale:inverted'}
            iconStyle={'triangle'}
            indentWidth={2}
            style={{ display: 'inline-block' }}
            name={props.name}
            collapsed={props.collapsed}
            enableClipboard={false}
            displayObjectSize={false}
            displayDataTypes={false}
            src={props.object}/>
    </div>
}
