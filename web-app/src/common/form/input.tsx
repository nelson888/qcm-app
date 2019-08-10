import React from 'react';
import ExpandableAnimated from "../components/expandableanimated";
import './input.scss';

type InputParameters = {
    value: any
    name: string,
    label: string,
    error: string,
    type: string,
    onChange?: any, //callback
    autoFocus?: boolean,
    placeholder?: string,
    width?: number|string,
    marginBottom?: number
};

const Input = ({value, name, label, error, type, onChange, autoFocus, placeholder, width, marginBottom=undefined }: InputParameters) => {

    return (
    <div className="form-container"
         style={{
             marginBottom
         }}
    >
        <label htmlFor={name} className="form-label">{label}</label>
        <input
            style={{
                marginTop: 5,
                marginBottom: 8,
                width: width
            }}
            name={name}
            className="form-input"
            value={value}
            onChange={onChange}
            type={type}
            autoFocus={autoFocus}
            placeholder={placeholder}
            id={name}/>
        {
          <ExpandableAnimated expanded={!!error}>
              {
                <p className="input-error no-margin no-padding"
                                style={{
                                    marginTop: 2,
                                    marginBottom: 8
                                }}
                  >{error}</p>
              }
          </ExpandableAnimated>
        }
    </div>
    );
};

export default Input;
