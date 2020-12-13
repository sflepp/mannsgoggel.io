import React from 'react';

import ReactJson from 'react-json-view'


const JSONObject = (props: { name: string, object: any, collapsed?: number }) => {
  return <ReactJson
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
}

export default JSONObject