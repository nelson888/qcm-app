import React, {Component} from 'react';
import './navbar.scss';
import {NavLink, Link} from 'react-router-dom';
import {
    BrowserView,
    MobileView,
    isMobile
} from "react-device-detect";
import ExpandableAnimated from "../common/components/expandableanimated";
type NavbarProps = {
    logged: boolean,
    loading: boolean,
    onLogOut(): void
};
const activeStyle = {
    textDecoration: "underline",
};

type State = {
    opened: boolean
};

type NavLinkProps = {
    name: string,
    path?: string,
    onClick?(): void,
    logout?: boolean
};

class NavBar extends Component<NavbarProps, State> {

    state: State = {
        opened: !isMobile
    };

    render() {
        const {logged, onLogOut, loading}: NavbarProps = this.props;
        return (
            <React.Fragment>

                <BrowserView>
                    <nav className="nav fixed-top">
                        {this.navLinks(loading, logged, onLogOut)}
                    </nav>
                </BrowserView>
                <MobileView>
                    <div className="mobile-menu-container no-margin no-padding">
                        <ExpandableAnimated expanded={this.state.opened}>
                            {this.navLinks(loading, logged, onLogOut)}
                        </ExpandableAnimated>
                    </div>
                    <nav className="nav fixed-top">
                        <ul>
                            <li>
                                <Link className="link title" to="/">
                                    <img src={require('../images/icon.png')} className="logo"  alt="logo" />
                                    Almaze Admin
                                </Link>
                            </li>

                        </ul>

                        {
                            !loading && <img src={require('../images/menu.png')} className="mobile-menu-icon logo"  alt="menu"
                                             onClick={this.toggleDrawer}
                            />
                        }
                    </nav>
                </MobileView>
            </React.Fragment>
        );
    };

    toggleDrawer = ():void => {
        this.setState({opened: !this.state.opened });
    };


    navLinks = (loading: boolean, logged: boolean, onLogOut: any):React.ReactElement => {
        let desktopAlmaze: React.ReactElement | null = !isMobile ? (
            <li>
                <Link className="link title" to="/">
                    <img src={require('../images/icon.png')} className="logo"  alt="logo" />
                    Almaze App Admin
                </Link>
            </li>
        ) : null;
        if (loading) {
            return isMobile ? <div/> : <ul>{desktopAlmaze}</ul>;
        }
        let ulClasses = isMobile ? "mobile-menu no-padding no-margin" : "";

        if (!logged) {
            return (
                <ul className={ulClasses}>
                    {!isMobile && desktopAlmaze}
                    {this.navLink({name: "Log In", path: "/login"})}
                </ul>
            );
        }
        let links = ["Notification", "Posts", "Products", "Designers"];
        let onLogOutClick = () => {
            onLogOut();
            if (isMobile) {
                this.toggleDrawer();
            }
        };
        return (
            <ul className={ulClasses}>
                {!isMobile && desktopAlmaze}
                { links.map(name => this.navLink({name })) }
                { this.navLink({name: "Log Out", path: "/login", logout: true, onClick: onLogOutClick})}
            </ul>
        );
    };

    navLink({name, path="/" + name.toLowerCase(), onClick, logout=false}:NavLinkProps): React.ReactElement {
        if (!onClick && isMobile) {
            onClick = this.toggleDrawer;
        }
        let classes: string = "link";
        if (logout) {
            classes += " logout";
        }
        return (
            <li className={isMobile ? "mobile-menu-item" : undefined}
                key={name}
            >
                <NavLink className={classes}
                         to={path}
                         onClick={onClick}
                         style={isMobile ? {paddingLeft: 8, width: '100%'} : undefined}
                         activeStyle={activeStyle}>{name}</NavLink>
            </li>
        );
    }
}

export default NavBar;
