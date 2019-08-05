import React, {Component} from 'react';
import './App.scss';
import { Route, Switch, Redirect } from "react-router-dom";
import LoadingSpinner from "./common/components/loadingspinner";
import LoginPage from "./pages/loginpage";
import NotFoundPage from "./pages/notfoundpage";
import {toast, ToastContainer} from "react-toastify";
import NavBar from "./components/navbar";
import {LoginResponse, MeResponse, QcmClient, TEACHER} from "./services/qcmClient";
import 'react-toastify/dist/ReactToastify.css';
import TeacherPage from "./pages/teacherpage";
import StudentPage from "./pages/studentpage";
import ApiClient from "./services/apiClient";
import {History} from "history";
import {deleteCookie, getCookie, setCookie} from "./common/util/cookieHandler";
import {AUTH_COOKIE} from "./util/constants";
import 'react-confirm-alert/src/react-confirm-alert.css';

type AppState = {
    loading: boolean
};

type AppProps = {
};

class App extends Component<AppProps, AppState> {

    apiClient: QcmClient = new ApiClient();
    state: AppState = {
        loading: true
    };
    componentDidMount(): void {
        const jwt: string = getCookie(AUTH_COOKIE);
        if (!jwt) {
            this.setState({loading: false});
        } else {
            this.apiClient.setJwt(jwt);
            this.apiClient.getMe()
                .then((response: MeResponse) => {
                    if (response.isSuccess) {
                        this.apiClient.setUser(response.successData);
                    } else {
                        this.apiClient.setJwt("");
                        deleteCookie(AUTH_COOKIE);
                    }
                    this.setState({loading: false});
                }).catch((error: any) => {
                this.apiClient.setJwt("");
                this.setState({loading: false});
                toast.error("An error occurred: " + error.toString());
            });
        }
    }


  render = () => {
    const loaded = !this.state.loading;
    const logged = this.apiClient.isLogged();
    return (
        <React.Fragment>
          <NavBar logged={logged} onLogOut={() => null} loading={!loaded} />
          <div className="content">
            <ToastContainer/>
            {
              !loaded && <LoadingSpinner/>
            }
            {
              loaded &&
              <React.Fragment>
                <Switch>
                  <Route exact path="/" render={() => (
                      logged ? (
                          <Redirect to="/home"/>
                      ) : (
                          <Redirect to="/login"/>
                      )
                  )}/>
                  <Route path="/home" exact render={(props)=> !this.apiClient.isLogged() ? <Redirect to="/login"/> : (this.apiClient.getRole() === TEACHER ? <TeacherPage apiClient={this.apiClient} {...props} /> : <StudentPage  apiClient={this.apiClient} {...props} />)} />
                  <Route path="/login" exact render={(props) => this.apiClient.isLogged()? <Redirect to="/home"/>  :  <LoginPage apiClient={this.apiClient} onLogin={this.onLogin} {...props} /> } />
                  <Route component={NotFoundPage} />
                </Switch>
              </React.Fragment>
            }
          </div>
        </React.Fragment>
    );
  };

  onLogin = (response: LoginResponse, history: History): void => {
      if (response.isSuccess) {
          toast.success("Successfully logged in");
          history.replace('/home');
          setCookie(AUTH_COOKIE, response.successData.jwt, new Date(response.successData.expires))
      } else {
          toast.error(response.errorData);
      }
  }
}

export default App;
