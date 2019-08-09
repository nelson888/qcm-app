import React from "react";
import FormComponent, { Form } from '../common/form/formComponent';
import './loginpage.scss';
import { toast } from 'react-toastify';
import {History} from "history";
import LoadingScreen from "../common/components/loadingscreen";
import {isMobile} from "react-device-detect";
import {LoginResponse, QcmClient} from "../services/qcmClient";

type LoginForm = {
    username: string,
    password: string
};

type LoginState = {
    form: LoginForm,
    errors: any,
    loading: boolean
}

type Props = {
    apiClient: QcmClient,
    history: History,
    onLogin(response: LoginResponse, history: History): void
};

class LoginPage extends FormComponent<Props, LoginForm> {
    state : LoginState = {
        form: {
            username: "",
            password: ""
        },
        errors: {},
        loading: false
    };

    render() {
        const {loading} = this.state;

        const responsiveStyle = {
            width: '80%',
            display: isMobile ? 'block': undefined,
            marginRight: isMobile ? 'auto' : undefined,
            marginLeft: isMobile ? 'auto' : 14,
        };

        return (
            <LoadingScreen
                active={loading}
                message={"Loading..."}
            >
                <div

                    style={responsiveStyle}
                >
                <h2

                    style={{
                        marginLeft: 0
                    }}
                >
                    Log In
                </h2>
                <Form onSubmit={this.handleSubmit}>

                    <div
                        style={responsiveStyle}
                    >
                    {this.renderInput({ name: "username", type: "text", placeholder: "Type your username" })}
                    {this.renderInput({ name: "password", type: "password", placeholder: "Type your password"})}
                    </div>

                    <button
                        className="btn-grad"

                    >Log In</button>

                </Form>
                </div>
            </LoadingScreen>
        );
    }

    onSubmit = (form:  LoginForm) => {
        const { username, password } = form;
        this.setState({loading: true });
        this.props.apiClient.logIn(username, password)
            .then((response: LoginResponse) => {
                this.setState({loading: false});
                this.props.onLogin(response, this.props.history);
            })
            .catch((error: any) => {
            this.setState({loading: false});
            toast.error("An error occurred: " + error.toString());
        });
    };


    validateProperty(form: any, name: string): any {
        let value: string = form[name];
        if (value.length === 0) {
            return  `You must enter the ${name}`;
        }
        if (name === "password" && value.length < 2) {
            return "The password is at least 2 characters long";
        }
    }
}
export default LoginPage;
