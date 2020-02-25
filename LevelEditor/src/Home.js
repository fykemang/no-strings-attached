import React, { useState, useRef } from 'react';
import { render } from 'react-dom';
import { Stage, Layer, Text, Image} from 'react-konva';
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import Rectangle from "./Rectangle";
import URLImage from "./URLImage";
import useImage from 'use-image';
import dude from "./assets/dude.png"


export default class Home extends React.Component  {

   
    addWall = () => {
        const rect = {
          x: 50,
          y: 50,
          width: 100,
          height: 100,
          fill: "black",
          id: `rect${this.state.walls.length + 1}`,
        };
        var wall = this.state.walls;
        wall.push(rect);
        this.setState({walls:wall})
     
        console.log(this.state.walls)
      };

      ifselected = (rect) => {
        console.log(this.state);
        return rect.id === this.state.selectedID;
      }
        
    state = {
     
        walls: [],
        couples:[],
        selectedID: null,
        
      };
      
  render(){ return(
  
    <React.Fragment>
   
    <ButtonGroup>
        <Button variant="secondary" onClick={this.addWall}>
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
  >
        <Stage width={800} height={600} 
         onMouseDown={e => {
            const clickedOnEmpty = e.target === e.target.getStage();
            if (clickedOnEmpty) {
             this.setState({selectedID: null});
            }
          }}
        >
        <Layer>
        {this.state.walls.map((rect, i) => {
            return (
              <Rectangle
                shapeProps={rect}     
                isSelected={
                    this.ifselected(rect)}  
                onSelect={() => {
                   this.setState({selectedID: rect.id})
                }} 
                onChange={newAttrs => {
                    const rects = this.state.walls.slice();
                    rects[i] = newAttrs;
                    this.setState({walls: rects})
                  }}      
              />
            );
          })}
        </Layer>
      </Stage>
      </div>
      </React.Fragment>
    )
    }

}