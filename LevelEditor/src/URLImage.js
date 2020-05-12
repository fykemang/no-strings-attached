import React from "react";
import { Image } from "react-konva";
import useImage from "use-image";

const URLImage = React.forwardRef((props, ref) => {
  const [image] = useImage(props.src);
  return (
    <Image
      draggable
      image={image}
      x={props.x}
      y={props.y}
      width={props.width}
      height={props.height}
      onClick={props.onClick}
      ref={ref}
      dragBoundFunc={(pos) => props.dragBoundFunc(pos)}
      onDragEnd={e => {
        props.onDragEnd(e.target.x(), e.target.y());
      }}
    />
  );
});
export default URLImage;
