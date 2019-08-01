import React, {Component} from 'react';
import './App.scss';
import { Route, Switch, Redirect } from "react-router-dom";
import LoadingSpinner from "./common/components/loadingspinner";
import LoginPage from "./pages/loginpage";
import NotFoundPage from "./pages/notfoundpage";
import {toast, ToastContainer} from "react-toastify";
import NavBar from "./components/navbar";
import {LoginResponse, QcmClient, TEACHER} from "./services/qcmClient";
import 'react-toastify/dist/ReactToastify.css';
import TeacherPage from "./pages/teacherpage";
import StudentPage from "./pages/studentpage";
import ApiClient from "./services/apiClient";
import {History} from "history";
import {setCookie} from "./common/util/cookieHandler";
import {AUTH_COOKIE} from "./util/constants";

type AppState = {

};

type AppProps = {
};

class App extends Component<AppProps, AppState> {

    componentDidMount(): void {
        console.log(Date.parse("2019-08-31T03:23:47.039+0000"));
    }

    apiClient: QcmClient = new ApiClient();

  render = () => {
    const loaded = true;
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
                  <Route path="/home" exact render={(props)=> !this.apiClient.isLogged() ? <Redirect to="/login"/> : (this.apiClient.getRole() === TEACHER ? <TeacherPage apiClient={this.apiClient} {...props} /> : <StudentPage {...props} />)} />
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
