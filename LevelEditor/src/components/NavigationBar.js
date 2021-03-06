import React from "react";
import { Link } from "react-router-dom";
import { Nav, Navbar } from "react-bootstrap"

export const NavigationBar = () => (
    <Navbar expand="lg">
      <Navbar.Brand href="/">Level Editor</Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="ml-auto">
          <Nav.Item>
              <Link to="/">Home</Link>
          </Nav.Item>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
);
