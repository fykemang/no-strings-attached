import React, { useState } from "react";
import { Stage, Layer } from "react-konva";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import Rectangle from "./Rectangle";
import URLImage from "./URLImage";
import DudeImage from "./assets/dude.png";

function Home() {
  const [c_pos, setPos] = useState(null);
  const [walls, setWalls] = useState([]);
  const [selectedId, selectShape] = useState(null);
  const [image, setImage] = useState(null);
  const [, updateState] = useState();
  const dragUrl = React.useRef();
  const stageRef = React.useRef();

  const handler = (xpos, ypos) => {
    setPos({
      x: xpos,
      y: ypos
    });
  };

  const downloadFile = async () => {
    const myData = {
      tiles: walls
    };
    const fileName = "file";
    const json = JSON.stringify(myData);
    const blob = new Blob([json], { type: "application/json" });
    const href = await URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = href;
    link.download = fileName + ".json";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const addWall = () => {
    const wall = {
      x: 10,
      y: 10,
      width: 100,
      height: 100,
      fill: "black",
      id: `rect${walls.length + 1}`
    };
    const rects = walls.concat([wall]);
    setWalls(rects);
  };

  const forceUpdate = React.useCallback(() => updateState({}), []);
  document.addEventListener("keydown", ev => {
    if (ev.code === "Backspace") {
      let index = walls.findIndex(r => r.id === selectedId);
      if (index !== -1) {
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
        <Button variant="secondary">Couple</Button>
      </ButtonGroup>
      <img
        alt="character"
        src={DudeImage}
        draggable="true"
        onDragStart={e => {
          dragUrl.current = e.target.src;
        }}
      />
      <input style={{ display: "none" }} type="file" />
      <div
        style={{
          backgroundColor: "lightgrey",
          width: "800px"
        }}
        onDrop={e => {
          // register event position
          stageRef.current.setPointersPositions(e);
          // add image
          setImage({
            ...stageRef.current.getPointerPosition(),
            src: dragUrl.current
          });
          setPos({
            x: stageRef.current.getPointerPosition().x,
            y: stageRef.current.getPointerPosition().y
          });
        }}
        onDragOver={e => e.preventDefault()}
      >
        <Stage
          width={800}
          height={600}
          onMouseDown={e => {
            const clickedOnEmpty = e.target === e.target.getStage();
            if (clickedOnEmpty) {
              selectShape(null);
            }
          }}
          style={{ border: "1px solid grey" }}
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
            {image != null && <URLImage image={image} draghandler={handler} />}
          </Layer>
        </Stage>
      </div>
      <Button variant="secondary" onClick={downloadFile}>
        Download
      </Button>
    </React.Fragment>
  );
}
export default Home;
