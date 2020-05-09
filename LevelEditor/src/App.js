import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Home from './Home.js';
import { NavigationBar } from './components/NavigationBar';

class App extends Component {
  render() {
    return (
      <>
        <Router basename="/no-strings-attached">
          <NavigationBar />
            <Switch>
              <Route exact path="/" component={Home} />
            </Switch>
        </Router>
      </>
    );
  }
}

export default App;
