import React, {Component} from 'react';
import {isEmpty} from "../util/functions";
import Input from "./input";

type render_input_parameters<T> = {
    name: string,
    label?: string,
    type: string,
    autoFocus?: boolean,
    onChange?(event: any): any,
    errors?: any,
    placeholder?: string,
    width?: number|string,
    valueLoader?(form: T): string
};
abstract class FormComponent<P, Form> extends Component<P, any> {

    /**
     * handle change on input
     * don't forget to set 'name' property on the input tag
     * @param event
     */
    onInputChange = (event: any) => {
        let form : any = {...this.state.form};
        let name = event.target.name;
        form[name] = event.target.value;

        let error: any = this.validateProperty(form, name);
        let errors: any = {...this.state.errors};
        if (errors[name]) {
            errors[name] = error;
        }

        this.setState({
            form,
            errors
        });
    };

    handleSubmit = (e: any) => {
        e.preventDefault(); //prevent submitting form to server
        let errors: any = {};
        let form: Form = this.state.form;
        Object.keys(form).forEach(key => {
            let error = this.validateProperty(form, key);
            if (error) {
                errors[key] = error;
            }
        });
        this.setState({
            errors
        });
        if (isEmpty(errors)) {
            this.onSubmit(form);
        }
    };


    renderInput = ({name, label = "", type, autoFocus = false, onChange = this.onInputChange, errors=this.state.errors, placeholder, width, valueLoader } : render_input_parameters<Form>): any => {
        if (!label) {
            label = name.charAt(0).toLocaleUpperCase() + name.substr(1);
        }
        const { form } = this.state;
        if (!type) type = "";
        return(
            <Input
                value={valueLoader ? valueLoader(form) : form[name]}
                name={name}
                label={label}
                type={type}
                width={width}
                onChange={onChange}
                placeholder={placeholder}
                error={errors ? errors[name]: ""}
                autoFocus={autoFocus}
            />
        );
    };

    abstract validateProperty(form: any, name: string) : any; //returns the error of a form property
    abstract onSubmit(form: Form) : void;
}

type FormProps = {
    onSubmit(e: any): void
    style?: object,
    children?: any
}

class Form extends Component<FormProps, {}> {

    render() {
        const {onSubmit, children} = this.props;
        return (
            <form onSubmit={onSubmit}>
                {children}
            </form>
        );
    }
}

export default FormComponent;
export { Form };
