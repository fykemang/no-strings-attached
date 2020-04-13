import React from "react";
import { Image } from "react-konva";
import useImage from "use-image";

const URLImage = (props) => {
  const [image] = useImage(props.src);
  return (
    <Image
      draggable
      image={image}
      x={props.x}
      y={props.y}
      width={props.width}
      height={props.height}
      // I will use offset to set origin to the center of the image
      offsetX={image ? image.width / 2 : 0}
      offsetY={image ? image.height / 2 : 0}
      onDragEnd={e => {
        props.onDrag(e.target.x(), e.target.y());
      }}
    />
  );
};
export default URLImage;
