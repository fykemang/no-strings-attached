import {Group, Line} from "react-konva";
import npcImageSrc from "./assets/npc.png";
import URLImage from "./URLImage";

function Couple(props) {

    return (
        < Group
    draggable >
    < URLImage
    {...
        props.leftNpc
    }
    src = {npcImageSrc}
    onDragEnd = {(x, y)
=>
    {
        props.onChange({x: x, y: y}, "left")
    }
}
    dragBoundFunc = {props.dragBoundFunc}
    />
    < URLImage
    {...
        props.rightNpc
    }
    src = {npcImageSrc}
    onDragEnd = {(x, y)
=>
    {
        props.onChange({x: x, y: y}, "right")
    }
}
    dragBoundFunc = {props.dragBoundFunc}
    />
    < Line
    points = {[props.leftNpc.x, props.leftNpc.y, props.rightNpc.x, props.rightNpc.y
]
}
    stroke = {"red"}
    lineJoin = {"round"}
    lineCap = {"round"}
    strokeWidth = {3}
    tension = {1}
        > < /Line>
        < /Group>
)
    ;
}

export default Couple;
