import React, { useState, useEffect, useReducer } from "react";
import { Stage, Layer, Line } from "react-konva";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import Dropdown from "react-bootstrap/Dropdown";
import DropdownButton from "react-bootstrap/DropdownButton";
import Container from "react-bootstrap/Container";
import { Row } from "react-bootstrap";
import Tile from "./Tile";
import URLImage from "./URLImage";
import player from "./assets/player.png";

const initialState = {
  tiles: [],
  npcs: [],
  items: [],
  player: {
    x: 500,
    y: 500,
  },
};

function reducer(state, action) {
  switch (action.type) {
    case "add-tile":
      const tile = {
        x: 10,
        y: 10,
        width: 100,
        height: 100,
        type: action["tile-type"],
        id: state.tiles.length,
      };
      const newTiles = state.tiles.slice();
      newTiles.push(tile)
      
      return {
        ...state,
        tiles: newTiles,
      };
    case "modify-tile":
      return {
        ...state,
        tiles: state.tiles.map((tile, index) => {
          if (index !== action.index) {
            return tile;
          }
          return {
            ...tile,
            ...action.attrs
          }
        })
      }
    case "move-player":
      return {
        ...state,
        player: {
          x: action.x,
          y: action.y,
        },
      };
    case "add-item":
      return {
        ...state,
        items: {
          x: 10,
          y: 10,
        },
      };

    default:
      throw new Error();
  }
}

function Home() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [c_pos, setPos] = useState(null);
  const [selectedId, selectShape] = useState(null);
  const [image, setImage] = React.useState(null);
  const dragUrl = React.useRef();
  const stageRef = React.useRef();
  const gridBlockSize = 20;
  const width = window.innerWidth;
  const height = window.innerHeight;

  const displayGrid = () => {
    const horizontalLines = [];
    for (let i = 0; i < width / gridBlockSize; i++) {
      horizontalLines.push(
        <Line
          key={`horizontal-line-${i}`}
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

    const verticalLines = [];
    for (let i = 0; i < height / gridBlockSize; i++) {
      verticalLines.push(
        <Line
          key={`vertical-line-${i}`}
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
    return horizontalLines.concat(verticalLines);
  };

  const downloadFile = async () => {
    const myData = {
      tiles: [],
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

  return (
    <Container fluid>
      <Row>
        <ButtonGroup>
          <DropdownButton title="Tiles">
            <Dropdown.Item
              onClick={() =>
                dispatch({ type: "add-tile", "tile-type": "normal" })
              }
            >
              Normal
            </Dropdown.Item>
            <Dropdown.Item
              onClick={() =>
                dispatch({ type: "add-tile", "tile-type": "spikes" })
              }
            >
              Spikes
            </Dropdown.Item>
          </DropdownButton>
          <Button variant="secondary">Couple</Button>
          <Button variant="secondary">Item</Button>
        </ButtonGroup>
      </Row>
      <Row>
        <Stage
          style={{ border: "1px solid grey" }}
          width={width}
          height={height}
          onMouseDown={(e) => {
            const clickedOnEmpty = e.target === e.target.getStage();
            if (clickedOnEmpty) {
              selectShape(null);
            }
          }}
          ref={stageRef}
        >
          <Layer>
            {displayGrid()}
            {state.tiles.map((rect, i) => {
              return (
                <Tile
                  key={i}
                  blockSize={gridBlockSize}
                  shapeProps={rect}
                  isSelected={rect.id === selectedId}
                  onSelect={() => {
                    selectShape(rect.id);
                  }}
                  onChange={(newAttrs) =>
                    dispatch({
                      type: "modify-tile",
                      index: rect.id,
                      attrs: newAttrs,
                    })
                  }
                />
              );
            })}
            <URLImage
              src={player}
              width={gridBlockSize * 3}
              height={gridBlockSize * 5.5}
              x={state.player.x}
              y={state.player.y}
              onDrag={(x, y) => {
                dispatch({ type: "move-player", x: x, y: y });
              }}
            />
          </Layer>
        </Stage>
      </Row>
      <Row>
        <Button variant="secondary" onClick={downloadFile}>
          Download
        </Button>
      </Row>
    </Container>
  );
}
export default Home;
