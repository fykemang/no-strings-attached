import React, {useRef} from "react";
import { Group, Line } from "react-konva";
import npcImageSrc from "./assets/npc.png";
import URLImage from "./URLImage";

function Couple(props) {
  const leftNpc = useRef(null);
  const rightNpc = useRef(null);

  return (
    <Group onClick={props.onSelect}>
      <URLImage
        {...props.leftNpc}
        src={npcImageSrc}
        onDragEnd={(x, y) => {props.onChange({x: x, y: y}, "left")}}
        xChange={e => {console.log(e.oldVal)}}
        dragBoundFunc={props.dragBoundFunc}
        ref={leftNpc}
      />
      <URLImage
        {...props.rightNpc}
        src={npcImageSrc}
        onDragEnd={(x, y) => {props.onChange({x: x, y: y}, "right")}}
        dragBoundFunc={props.dragBoundFunc}
        ref={rightNpc}
      />
      <Line
        points={[props.leftNpc.x + 20, props.leftNpc.y + 50, props.rightNpc.x + 20, props.rightNpc.y + 50]}
        stroke={"red"}
        lineJoin={"round"}
        lineCap={"round"}
        strokeWidth={3}
        tension={1}
      ></Line>
    </Group>
  );
}

export default Couple;
