import React from "react";
import './checkbox.scss';
type Props = {
    checked: boolean,
    onValueChange(): void,
    size?: number,
    className?: string
}
const checkedImage = require('./checked.png');
const Checkbox = ({checked, onValueChange, size=20, className=""}: Props) => {
    const checkedSize: number = size * 0.8;
    return (
        <div className={"simple-checkbox " + className}
             style={{
                 width: size,
                 height: size
             }}
             onClick={onValueChange}
        >
            {
                checked && <img
                    alt="checked"
                    src={checkedImage}
                    className='simple-checkbox-checked center-horizontal unselectable'
                    style={{
                        width: checkedSize,
                        height: checkedSize,
                        paddingTop: size * 0.15
                    }}
                />
            }
        </div>
    );
};

export default Checkbox;
