<<<<<<< HEAD
import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Home from './Home.js';
import { Layout } from './componets/Layout';
import { NavigationBar } from './componets/NavigationBar';
// const { ReactDraggable: Draggable } = window;
=======
import React, { Component } from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Home from "./Home.js";
import { Layout } from "./components/Layout";
import { NavigationBar } from "./components/NavigationBar";
>>>>>>> af5e5c31623add54e6f1ff309dc141799f186e91


class App extends React.Component {
  render() {
    return (
      <React.Fragment>
        <Router>
          <NavigationBar />
          <Layout>
            <Switch>
              <Route exact path="/" component={Home} />
            </Switch>
          </Layout>
        </Router>
      </React.Fragment>
    );
  }
}

export default App;
