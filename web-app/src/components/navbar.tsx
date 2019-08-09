import React, {Component} from 'react';
import './navbar.scss';
import {NavLink, Link} from 'react-router-dom';
type NavbarProps = {
    logged: boolean,
    loading: boolean,
    onLogOut(): void
};
const activeStyle = {
    textDecoration: "underline",
};

type State = {
};

type NavLinkProps = {
    name: string,
    path?: string,
    onClick?(): void,
    logout?: boolean
};

class NavBar extends Component<NavbarProps, State> {

    render() {
        const {logged, onLogOut, loading}: NavbarProps = this.props;
        return (
            <React.Fragment>
                <nav className="nav fixed-top">
                    <ul>
                        <li>
                            <Link className="link title" to="/">
                                <img src={require('../images/barLogo.png')} className="logo"  alt="logo" />
                                MCQ App
                            </Link>
                        </li>
                        {
                            !loading && logged &&
                            <li>
                                <NavLink className="link logout"
                                         to="/login"
                                         onClick={onLogOut}
                                         activeStyle={activeStyle}>Log Out</NavLink>
                            </li>
                        }
                    </ul>
                </nav>
            </React.Fragment>
        );
    };
}

export default NavBar;
