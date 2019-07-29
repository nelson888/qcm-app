import React from "react";
import './checkboxrendererfactory.scss';
import {createGlobalStyle} from "styled-components";

type RendererProps = {
    name?: string,
    id?: string,
}

type Props = {
    checked: boolean,
    onChange(checked: boolean): void,
    disabled?: boolean,
}
const newCheckboxRenderer = ({name="cbx", id=name}: RendererProps): (p: Props) => React.ReactElement => {
    const GlobalStyle = createGlobalStyle`
#${id}:checked + .toggle:before {
  background: #947ADA;
}
#${id}:checked + .toggle span {
  background: #4F2EDC;
  transform: translateX(20px);
  transition: all 0.2s cubic-bezier(0.8, 0.4, 0.3, 1.25), background 0.15s ease;
  box-shadow: 0 3px 8px rgba(79, 46, 220, 0.2);
}
#${id}:checked + .toggle span:before {
  transform: scale(1);
  opacity: 0;
  transition: all 0.4s ease;
}
`;

    return (({disabled=false, onChange, checked}: Props) => (
        <React.Fragment>
            <GlobalStyle />
            <input type="checkbox" id={id} style={{display: "none"}} name={name}
                   checked={checked}
                   onChange={() => onChange(disabled)}
                   disabled={disabled}
            />
            <label htmlFor={id} className="toggle"><span></span></label>
        </React.Fragment>
    ));
};

export default newCheckboxRenderer;
