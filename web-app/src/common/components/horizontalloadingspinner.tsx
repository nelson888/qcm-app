import React from 'react';
import './horizontaloadingspinner.scss';


type Props = {
    zoom?: number
}
const HorizontalLoadingSpinner = ({zoom=1}: Props) => {

    let divs :any[] = [];
    for (let i = 0; i < 4; i++) {
        divs.push(<div key={i.toString()}/>);
    }
    return <div className="lds-ellipsis"
    style={{zoom: zoom}}>
        {divs}
    </div>;

};

export default HorizontalLoadingSpinner;
