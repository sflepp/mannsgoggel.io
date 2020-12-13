import { Popover, Tag } from "antd";
import React from "react";
import JSONObject from "./JSONObject";

interface Props {
  name: string,
  object: string,
  isLast: boolean,
}

const DebugParameter = (props: Props) => {

  let json;
  if (props.object === undefined) {
    json = 'undefined'
  } else {
    if (typeof JSON.parse(props.object) === 'object') {
      json = <JSONObject name={null} object={
        JSON.parse(props.object)
      }/>
    } else {
      json = <span style={{ fontFamily: 'monospace' }}>{props.object}</span>;
    }
  }

  const content = (
    <div style={{ width: '500px', maxHeight: '400px', overflow: 'auto' }}>
      {json}
    </div>
  )

  return <span>
        <Popover
          content={content}
          title={`Variable '${props.name}'`}
          trigger='hover'
          placement='topLeft'
        >
          <Tag color='blue' style={{ marginLeft: '5px', marginRight: '-8px' }}>{props.name}</Tag> {!props.isLast && ','}
        </Popover>
  </span>
}

export default DebugParameter