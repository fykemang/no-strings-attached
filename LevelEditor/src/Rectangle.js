import React from 'react';
import { Rect, Transformer } from 'react-konva';
import Konva from 'konva';

// var width = window.innerWidth;
// var height = window.innerHeight;
// var shadowOffset = 20;
// var tween = null;
// var blockSnapSize = 30;

// var shadowRectangle = new Konva.Rect({
//   x: 0,
//   y: 0,
//   width: blockSnapsize * 6,
//   height: blockSnapSize * 3,
//   fill: '#FF7B17',
//   opacity: 0.6,
//   stroke: '#CF6412',
//   strokeWidth: 3,
//   dash: [20, 2]
// });

// function newRectangle(x, y, layer, stage) {
//   let rectangle = new Konva.Rect({

//   })
// }

const Rectangle = ({ shapeProps, isSelected, onSelect, onChange }) => {
  const shapeRef = React.useRef();
  const trRef = React.useRef();
  const blockSize = 50;

  React.useEffect(() => {
    if (isSelected) {
      // we need to attach transformer manually
      trRef.current.setNode(shapeRef.current);
      trRef.current.getLayer().batchDraw();
    }
  }, [isSelected]);

  return (
    <>
      <Rect
        onClick={onSelect}
        ref={shapeRef}
        {...shapeProps}
        draggable
        onDragEnd={e => {
          onChange({
            ...shapeProps,
            x: Math.round(e.target.x() / blockSize) * blockSize,
            y: Math.round(e.target.y() / blockSize) * blockSize,
          });
        }}
        onTransformEnd={e => {
          // transformer is changing scale of the node
          // and NOT its width or height
          // but in the store we have only width and height
          // to match the data better we will reset scale on transform end
          const node = shapeRef.current;
          const scaleX = node.scaleX();
          const scaleY = node.scaleY();

          // we will reset it back
          node.scaleX(1);
          node.scaleY(1);
          onChange({
            ...shapeProps,
            x: Math.round(e.target.x() / blockSize) * blockSize,
            y: Math.round(e.target.y() / blockSize) * blockSize,
            // set minimal value
            width: Math.max(5, node.width() * scaleX),
            height: Math.max(node.height() * scaleY),
          });
        }}
      />
      {isSelected && (
        <Transformer
          ref={trRef}
          boundBoxFunc={(oldBox, newBox) => {
            // limit resize
            if (newBox.width < 5 || newBox.height < 5) {
              return oldBox;
            }
            return newBox;
          }}
        />
      )}
    </>
  );
};

export default Rectangle;
