import React from 'react';
import './loadingspinner.scss';


type Props = {
    zoom?: number
}
const LoadingSpinner = ({zoom=1}: Props) => {

    let divs :any[] = [];
    for (let i = 0; i < 8; i++) {
        divs.push(<div key={i.toString()}/>);
    }
    return <div className="lds-roller screen-centered"
    style={{zoom: zoom}}>
        {divs}
    </div>;

};

export default LoadingSpinner;
