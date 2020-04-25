import React, { useState, useReducer, useCallback } from "react";
import { Stage, Layer, Line } from "react-konva";
import { ButtonGroup, Button, Dropdown, DropdownButton } from "react-bootstrap";
import Tile from "./Tile";
import URLImage from "./URLImage";
import PlayerTexture from "./assets/player.png";
import Couple from "./Couple";
import ItemTexture from "./assets/skein.png";
import ExitTexture from "./assets/exit.png";
import useEventListener from "./util/UseEventListener";

const initialState = {
  tiles: [],
  couples: [],
  items: [],
  player: {
    x: 500,
    y: 500,
  },
  exit: {
    x: 500,
    y: 500,
  },
};

function reducer(state, action) {
  switch (action.type) {
    case "delete-object":
      return {
        ...state,
        [action["delete-type"]]: state[action["delete-type"]].filter(
          (obj) => obj.id !== action.index
        ),
      };
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
      newTiles.push(tile);

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
            ...action.attrs,
          };
        }),
      };
    case "move-player":
      return {
        ...state,
        player: {
          x: action.x,
          y: action.y,
        },
      };
    case "move-exit":
      return {
        ...state,
        exit: {
          x: action.x,
          y: action.y,
        },
      };
    case "add-item":
      const item = {
        x: 500,
        y: 500,
        id: state.items.length,
      };
      const newItems = state.items.slice();
      newItems.push(item);

      return {
        ...state,
        items: newItems,
      };
    case "modify-item":
      return {
        ...state,
        items: state.items.map((item, index) => {
          if (index !== action.index) {
            return item;
          }
          return {
            ...item,
            ...action.attrs,
          };
        }),
      };
    case "add-couple":
      const leftNpc = {
        x: 500,
        y: 500,
        isSliding: action.isSliding,
        isRotating: action.isRotating,
        center: {},
        width: action.blockSize * 2,
        height: action.blockSize * 4,
        degree: 0,
        id: state.couples.length,
        leftPos: {},
        rightPos: {},
      };

      const rightNpc = {
        ...leftNpc,
        x: 600,
      };

      const newCouples = state.couples.slice();
      newCouples.push({ leftNpc, rightNpc, id: state.couples.length });
      return {
        ...state,
        couples: newCouples,
      };
    case "modify-couple":
      let updatedCouple = null;
      if (action.choice === "left") {
        updatedCouple = {
          leftNpc: { ...state.couples[action.index].leftNpc, ...action.attrs },
        };
      } else {
        updatedCouple = {
          rightNpc: {
            ...state.couples[action.index].rightNpc,
            ...action.attrs,
          },
        };
      }

      return {
        ...state,
        couples: state.couples.map((couple, index) => {
          if (index !== action.index) {
            return couple;
          }
          return {
            ...couple,
            ...updatedCouple,
          };
        }),
      };
    default:
      throw new Error();
  }
}

function Home() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [selectedNodeID, setSelectedNode] = useState(null);
  const [selectedNodeType, setSelectedNodeType] = useState("");
  const [canvasWidth, setCanvasWidth] = useState(window.innerWidth);
  const [canvasHeight, setCanvasHeight] = useState(window.innerHeight);
  const stageRef = React.useRef();
  useEventListener("keydown", (e) => {
    if (e.keyCode === 8) {
      dispatch({
        type: "delete-object",
        index: selectedNodeID,
        "delete-type": selectedNodeType,
      });
    }
  });
  const gridBlockSize = 20;

  const displayGrid = () => {
    const horizontalLines = [];
    for (let i = 0; i < canvasWidth / gridBlockSize; i++) {
      horizontalLines.push(
        <Line
          key={`horizontal-line-${i}`}
          points={[
            Math.round(i * gridBlockSize) + 0.5,
            0,
            Math.round(i * gridBlockSize) + 0.5,
            canvasWidth,
          ]}
          stroke={"#ddd"}
          strokeWidth={1}
        />
      );
    }

    const verticalLines = [];
    for (let i = 0; i < canvasHeight / gridBlockSize; i++) {
      verticalLines.push(
        <Line
          key={`vertical-line-${i}`}
          points={[
            0,
            Math.round(i * gridBlockSize),
            canvasWidth,
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
    const xScale = 37.5;
    const yScale = 44.4;
    const data = {
      type: "city",
      items: state.items.map((item) => {
        return {
          ...item,
          x: item.x / xScale,
          y: item.y / yScale,
        };
      }),
      tiles: state.tiles.map((tile) => {
        return {
          ...tile,
          x: tile.x / xScale,
          y: tile.y / yScale,
          height: tile.height / yScale,
          width: tile.width / xScale,
        };
      }),
      npc: state.couples.reduce((acc, couple) => {
        const leftNpc = {
          ...couple.leftNpc,
          x: couple.leftNpc.x / xScale,
          y: couple.leftNpc.y / yScale,
        };
        const rightNpc = {
          ...couple.rightNpc,
          x: couple.rightNpc.x / xScale,
          y: couple.rightNpc.y / yScale,
        };
        acc.push(leftNpc);
        acc.push(rightNpc);
        return acc;
      }, []),
      player: {
        x: state.player.x / xScale,
        y: state.player.y / yScale,
      },
      exit: {
        x: state.exit.x / xScale,
        y: state.exit.y / yScale,
      },
    };

    const fileName = "level";
    const json = JSON.stringify(data);
    const blob = new Blob([json], { type: "application/json" });
    const href = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = href;
    link.download = fileName + ".json";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const dragBoundFunc = useCallback((pos) => {
    const newY = pos.y < 0 ? 0 : pos.y > canvasHeight ? canvasHeight : pos.y;
    const newX = pos.x < 0 ? 0 : pos.x > canvasWidth ? canvasWidth : pos.x;
    return {
      x: newX,
      y: newY,
    };
  }, [canvasHeight, canvasWidth]);

  return (
    <div className="home-wrapper">
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
        <Button
          variant="secondary"
          onClick={() =>
            dispatch({
              type: "add-couple",
              isSliding: false,
              isRotating: false,
              blockSize: gridBlockSize,
            })
          }
        >
          Couple
        </Button>
        <Button
          variant="secondary"
          onClick={() =>
            dispatch({
              type: "add-item",
            })
          }
        >
          Item
        </Button>
      </ButtonGroup>
      <div className="canvas-container">
        <Stage
          draggable
          width={canvasWidth}
          height={canvasHeight}
          onMouseDown={(e) => {
            const clickedOnEmpty = e.target === e.target.getStage();
            if (clickedOnEmpty) {
              setSelectedNode(null);
            }
          }}
          onDragEnd={(x, y) => {
            console.log(x);
            console.log(x + " " + y);
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
                  isSelected={rect.id === selectedNodeID}
                  onSelect={() => {
                    setSelectedNode(rect.id);
                    setSelectedNodeType("tiles");
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
            {state.items.map((item, i) => {
              return (
                <URLImage
                  draggable
                  src={ItemTexture}
                  key={i}
                  width={gridBlockSize * 3}
                  height={gridBlockSize * 3}
                  dragBoundFunc={dragBoundFunc}
                  onClick={() => {
                    setSelectedNode(item.id);
                    setSelectedNodeType("items");
                  }}
                  isSelected={item.id === selectedNodeID}
                  x={item.x}
                  y={item.y}
                  id={item.id}
                  onDragEnd={(x, y) => {
                    dispatch({
                      index: item.id,
                      type: "modify-item",
                      attrs: { x: x, y: y },
                    });
                  }}
                />
              );
            })}
            {state.couples.map((couple, i) => {
              return (
                <Couple
                  isSelected={couple.id === selectedNodeID}
                  key={i}
                  blockSize={gridBlockSize}
                  onSelect={() => {
                    setSelectedNode(couple.id);
                    setSelectedNodeType("couples");
                  }}
                  leftNpc={couple.leftNpc}
                  rightNpc={couple.rightNpc}
                  dragBoundFunc={dragBoundFunc}
                  onChange={(attrs, choice) =>
                    dispatch({
                      type: "modify-couple",
                      index: couple.id,
                      attrs: attrs,
                      choice: choice,
                    })
                  }
                />
              );
            })}
            <URLImage
              src={ExitTexture}
              width={gridBlockSize * 3}
              height={gridBlockSize * 5}
              dragBoundFunc={dragBoundFunc}
              x={state.exit.x}
              y={state.exit.y}
              onDragEnd={(x, y) => dispatch({ type: "move-exit", x: x, y: y })}
            />
            <URLImage
              src={PlayerTexture}
              width={gridBlockSize * 2}
              height={gridBlockSize * 4}
              dragBoundFunc={dragBoundFunc}
              x={state.player.x}
              y={state.player.y}
              onDragEnd={(x, y) =>
                dispatch({ type: "move-player", x: x, y: y })
              }
            />
          </Layer>
        </Stage>
      </div>
      <Button variant="secondary" onClick={downloadFile}>
        Download
      </Button>
    </div>
  );
}
export default Home;
