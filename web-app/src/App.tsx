import React, {Component} from 'react';
import './App.scss';
import { Route, Switch, Redirect } from "react-router-dom";
import LoadingSpinner from "./common/components/loadingspinner";
import LoginPage from "./pages/loginpage";
import NotFoundPage from "./pages/notfoundpage";
import {ToastContainer} from "react-toastify";
import NavBar from "./components/navbar";
import {ApiClient, QcmClient, TEACHER} from "./services/apiClient";
import 'react-toastify/dist/ReactToastify.css';
import TeacherPage from "./pages/teacherpage";
import StudentPage from "./pages/studentpage";

type AppState = {

};

type AppProps = {
};

class App extends Component<AppProps, AppState> {

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
                  <Route path="/home" exact render={(props)=> !this.apiClient.isLogged() ? <Redirect to="/login"/> : (this.apiClient.getRole() === TEACHER ? <TeacherPage {...props} /> : <StudentPage {...props} />)} />
                  <Route path="/login" exact render={(props) => this.apiClient.isLogged()? <Redirect to="/home"/>  :  <LoginPage apiClient={this.apiClient} {...props} /> } />
                  <Route component={NotFoundPage} />
                </Switch>
              </React.Fragment>
            }
          </div>
        </React.Fragment>
    );
  }
}

export default App;
