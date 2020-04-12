import React, { useState } from "react";
import { Stage, Layer, Text, Image, Line } from "react-konva";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import Rectangle from "./Rectangle";
import URLImage from "./URLImage";
import dude from "./assets/dude.png";

function Home() {
  const [c_pos, setPos] = useState(null);
  const [walls, setWalls] = useState([]);
  const [selectedId, selectShape] = useState(null);
  const [image, setImage] = React.useState(null);
  const dragUrl = React.useRef();
  const stageRef = React.useRef();
  const gridBlockSize = 30;
  const width = window.innerWidth;
  const height = window.innerHeight;

  const handler = (xpos, ypos) => {
    setPos({
      x: xpos,
      y: ypos,
    });
  };

  const downloadFile = async () => {
    const myData = {
      tiles: walls,
    };
    const fileName = "level";
    const json = JSON.stringify(myData);
    const blob = new Blob([json], { type: "application/json" });
    const href = URL.createObjectURL(blob);
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
      id: `rect${walls.length + 1}`,
    };
    setWalls([...walls, wall]);
  };

  const addGrid = () => {
    const horizontalLines = [];
    const verticalLines = [];
    for (let i = 0; i < width / gridBlockSize; i++) {
      horizontalLines.push(
        <Line
          points={[
            Math.round(i * gridBlockSize) + 0.5,
            0,
            Math.round(i * gridBlockSize) + 0.5,
            height,
          ]}
          stroke={"#ddd"}
          strokeWidth={1}
        />
      );
    }

    for (let i = 0; i < height / gridBlockSize; i++) {
      verticalLines.push(
        <Line
          points={[
            0,
            Math.round(i * gridBlockSize),
            width,
            Math.round(i * gridBlockSize),
          ]}
          stroke={"#ddd"}
          strokeWidth={1}
        />
      );
    }

    return (
      <>
        {horizontalLines}
        {verticalLines}
      </>
    );
  };

  return (
    <>
      <ButtonGroup>
        <Button variant="secondary" onClick={addWall}>
          Wall
        </Button>
        <Button variant="secondary">Couple</Button>
        <Button variant="secondary">Items</Button>
      </ButtonGroup>
      <img
        alt="character"
        src={dude}
        draggable="true"
        onDragStart={(e) => {
          dragUrl.current = e.target.src;
        }}
      />
      <input style={{ display: "none" }} type="file" />
      <Stage
        width={width}
        height={height}
        onMouseDown={(e) => {
          const clickedOnEmpty = e.target === e.target.getStage();
          if (clickedOnEmpty) {
            selectShape(null);
          }
        }}
        style={{ border: "1px solid grey" }}
        ref={stageRef}
      >
        <Layer>
          {addGrid()}
          {walls.map((rect, i) => {
            return (
              <Rectangle
                key={i}
                shapeProps={rect}
                isSelected={rect.id === selectedId}
                onSelect={() => {
                  selectShape(rect.id);
                }}
                onChange={(newAttrs) => {
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
      <Button variant="secondary" onClick={downloadFile}>
        Download
      </Button>
    </>
  );
}
export default Home;
