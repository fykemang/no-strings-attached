import React, { useState } from "react";
import { Layer, Stage } from "react-konva";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import Rectangle from "./Rectangle";
import URLImage from "./URLImage";
import dude from "./assets/dude.png";
import Konva from "konva";

function Home() {
  const [c_pos, setPos] = useState(null);
  const [walls, setWalls] = useState([]);
  const [selectedId, selectShape] = useState(null);
  const [image, setImage] = useState(null);
  const [, updateState] = useState();
  const dragUrl = React.useRef();
  const stageRef = React.useRef();
  const { ReactDraggable: Draggable } = window;

  const handler = (xpos, ypos) => {
    setPos({
      x: xpos,
      y: ypos
    });
  };
  const lineStyle = {
    stroke: "lightcoral",
    strokeWidth: 1,
    strokeDasharray: 5.5
  };

  var width = window.innerWidth;
  var height = window.innerHeight;
  var shadowOffset = 20;
  var tween = null;
  var blockSnapSize = 30;

  var shadowRectangle = new Konva.Rect({
    x: 0,
    y: 0,
    width: blockSnapSize * 6,
    height: blockSnapSize * 3,
    fill: '#FF7B17',
    opacity: 0.6,
    stroke: '#CF6412',
    strokeWidth: 3,
    dash: [20, 2]
  });

  function newRectangle(x, y, layer, stage) {
    let rectangle = new Konva.Rect({
      x: x,
      y: y,
      width: blockSnapSize * 6,
      height: blockSnapSize * 3,
      fill: '#fff',
      stroke: '#ddd',
      strokeWidth: 1,
      shadowColor: 'black',
      shadowBlur: 2,
      shadowOffset: { x: 1, y: 1 },
      shadowOpacity: 0.4,
      draggable: true
    });
    rectangle.on('dragstart', (e) => {
      shadowRectangle.show();
      shadowRectangle.moveToTop();
      rectangle.moveToTop();
    });
    rectangle.on('dragend', (e) => {
      rectangle.position({
        x: Math.round(rectangle.x() / blockSnapSize) * blockSnapSize,
        y: Math.round(rectangle.y() / blockSnapSize) * blockSnapSize
      });
      stage.batchDraw();
      shadowRectangle.hide();
    });
    rectangle.on('dragmove', (e) => {
      shadowRectangle.position({
        x: Math.round(rectangle.x() / blockSnapSize) * blockSnapSize,
        y: Math.round(rectangle.y() / blockSnapSize) * blockSnapSize
      });
      stage.batchDraw();
    });
    layer.add(rectangle);
  }

  var stage = new Konva.Stage({
    container: 'container',
    width: width,
    height: height
  });

  const gridLayer = new Konva.Layer();
  const padding = blockSnapSize;
  console.log(width, padding, width / padding);
  for (let i = 0; i < width / padding; i++) {
    gridLayer.add(
      new Konva.Line({
        points: [
          Math.round(i * padding) + 0.5,
          0,
          Math.round(i * padding) + 0.5,
          height
        ],
        stroke: "#ddd",
        strokeWidth: 1
      })
    );
  }

  gridLayer.add(new Konva.Line({ points: [0, 0, 10, 10] }));
  for (let j = 0; j < height / padding; j++) {
    gridLayer.add(
      new Konva.Line({
        points: [0, Math.round(j * padding), width, Math.round(j * padding)],
        stroke: "#ddd",
        strokeWidth: 0.5
      })
    );
  }

  var layer = new Konva.Layer();
  // shadowRectangle.hide();
  // layer.add(shadowRectangle);
  // newRectangle(blockSnapSize * 3, blockSnapSize * 3, layer, stage);
  // newRectangle(blockSnapSize * 10, blockSnapSize * 3, layer, stage);

  // stage.add(gridLayer);
  // stage.add(layer);

  // canvas.on('mouse:up', fun);
  // var canvas = new fabric.Canvas('c', { selection: false });
  // var grid = 50;

  // for (var i = 0; i < Screen.width / grid; i++) {
  //   canvas.add(new fabric.Line([i * grid, 0, i * grid, 600],
  //     { stroke: '#ccc', selectable: false }));
  //   canvas.add(new fabric.Line([0, i * grid, 600, i * grid],
  //     { stroke: '#ccc', selectable: false }));
  // }

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
    // canvas.add(new fabric.Rect({
    //   left: 10,
    //   top: 10,
    //   width: 100,
    //   height: 100,
    //   fill: "#faa",
    //   originX: "left",
    //   originY: "top",
    //   centeredRotation: true,
    //   id: `rect${walls.length + 1}`
    // }));
    const rects = walls.concat([wall]);
    setWalls(rects);
  };

  const forceUpdate = React.useCallback(() => updateState({}), []);
  document.addEventListener("keydown", ev => {
    if (ev.code == "Backspace") {
      let index = walls.findIndex(r => r.id == selectedId);
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
        <Button variant="secondary">Couple</Button>
      </ButtonGroup>
      <img
        alt="character"
        src={dude}
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
                // <Draggable grid={[50, 50]}>
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
                // </Draggable>
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
