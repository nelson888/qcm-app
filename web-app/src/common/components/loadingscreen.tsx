import React from 'react';
import './loadingscreen.scss';
import HorizontalLoadingSpinner from "./horizontalloadingspinner";

type Props = {
    children: any,
    zoom?: number
    message?: string,
    active: boolean
}
const LoadingScreen = ({active, children, zoom=1.5, message=""}: Props) => {

    return <div
    style={{
        width: '100%',
        height: '100%',
        margin: 0,
        padding: 0
    }}
    className={active ? "input-disabled" : undefined}
    >
        {children}

        {
            active &&
                <div className="fade-in">
                    <div
                        style={{
                            position: 'fixed',
                            top: 0,
                            left: 0,
                            width: '100%',
                            height: '100%',
                            background: active? '#000000aa' : undefined
                        }}
                    />
                    <div className="screen-centered">
                        <h2
                            style={{color: "#fff",
                                textAlign: "center"}}
                        >{message}</h2>
                        <div
                            className="loading-center-horizontal">
                            <HorizontalLoadingSpinner
                                zoom={zoom}
                            />
                        </div>
                    </div>
                </div>
        }
    </div>;

};

export default LoadingScreen;
