import React, { useState, useRef } from 'react';
import { render } from 'react-dom';
import { Stage, Layer, Text, Image} from 'react-konva';
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import Rectangle from "./Rectangle";
import URLImage from "./URLImage";
import dude from "./assets/dude.png"

function Home(){
  const [walls, setWalls] = useState([]);
  const [selectedId, selectShape] = useState(null);
  const [image, setImage] = React.useState(null);
  const [, updateState] = React.useState();
  const dragUrl = React.useRef();
  const stageRef = React.useRef();

 
  const addWall = () => {
    const wall = {
      x: 10,
      y: 10,
      width: 100,
      height: 100,
      fill: "black",
      id: `rect${walls.length + 1}`,
    };
    const rects = walls.concat([wall]);
    setWalls(rects); 
  };

  const loadImage = (image) => {

    return (
    image!= null &&
    <URLImage image={image} />  
    )
  } 

  const forceUpdate = React.useCallback(() => updateState({}), []);
  document.addEventListener("keydown", ev => {
   
    if (ev.code == "Backspace") {
     let index = walls.findIndex(r => 
      r.id == selectedId);
      if (index != -1) {
        walls.splice(index, 1);
        setWalls(walls);
      } 
    }
    forceUpdate();
  });

  return (
    <React.Fragment>   
    <ButtonGroup>
        <Button variant="secondary" onClick={addWall}>
         Wall
        </Button>
        <Button variant="secondary" >
          Couple
        </Button>
        <img
            alt="character"
            src={dude}
            draggable="true"
            onDragStart={e => {
              dragUrl.current = e.target.src;
            }}
        />
      </ButtonGroup>
      <input
        style={{ display: "none" }}
        type="file"
      />
    <div
    style={{
      backgroundColor: "lightgrey",
      width: "800px"  
    }}
    onDrop={e => {
      // register event position
      stageRef.current.setPointersPositions(e);
      // add image
      setImage(
        
          {
            ...stageRef.current.getPointerPosition(),
            src: dragUrl.current
          }
        
      );
    }}
    onDragOver={e => e.preventDefault()}
  >  
     <Stage width={800} height={600} 
         onMouseDown={e => {
            const clickedOnEmpty = e.target === e.target.getStage();
            if (clickedOnEmpty) {
             selectShape(null);
            }
          }}
          style={{ border: '1px solid grey' }}
          ref={stageRef}
        >
        <Layer>
        {walls.map((rect, i) => {
            return (
              <Rectangle
                key={i}
                shapeProps={rect}
                isSelected={rect.id === selectedId}
                onSelect={() => {
                  selectShape(rect.id);
                }}
                onChange={newAttrs => {
                  const rects = walls.slice();
                  rects[i] = newAttrs;
                  setWalls(rects);
                }}
              />
            );
          })}
          { 
            loadImage(image)
          }         
        </Layer>
      </Stage>
      </div>
      </React.Fragment>
  )

}
export default Home;
