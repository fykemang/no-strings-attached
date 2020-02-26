import React from 'react';
import { Image } from 'react-konva';
import useImage from 'use-image';
import Home from './Home'


const URLImage = ({ image, draghandler }) => {
  
  const [img] = useImage(image.src);
  return (
      <Image
      draggable
      image={img}
      x={image.x}
      y={image.y}
      // I will use offset to set origin to the center of the image
      offsetX={img ? img.width / 2 : 0}
      offsetY={img ? img.height / 2 : 0}
      onDragEnd={e => {
       draghandler(e.target.x, e.target.y)
      }}
       />
  );
};
export default URLImage