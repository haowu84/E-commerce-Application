import React from 'react';
import { Button, TextInput, StyleSheet, View } from 'react-native';
import Axios from "axios";

async function register(registerRequest) {
    const requestBody = {
        email: registerRequest.email,
        password: registerRequest.password
    };

    const options = {
        method: "POST",
        baseURL: "http://10.0.2.2:8082",
        url: "/register",
        data: requestBody
    }

    return Axios.request(options);
}


const RegisterScreen = ( {navigation} ) => {
    const [email, onChangeEmail] = React.useState(null);
    const [password, onChangePassword] = React.useState(null);

    const submitRegister = (email, password) => {   
        const payLoad = {
            email: email,
            password: password.split('')
        }
    
        register(payLoad)
            .then(response => {
                alert("Registration succeeded");
                navigation.navigate("Login");
            })
            .catch(error => {
                if (error.response.data.result.code == 1002 || error.response.data.result.code == 1003) {
                    alert("Email address is invalid");
                } else if (error.response.data.result.code == 1000 || error.response.data.result.code == 1001) {
                    alert("Password is invalid")
                }
            })
    }
  
    return (
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                onChangeText={onChangeEmail}
                placeholder="Email"
                value={email}
            />
            <TextInput
                style={styles.input}
                onChangeText={onChangePassword}
                value={password}
                placeholder="Password"
            />
            <View style={styles.buttonContainer}>
                <Button
                    onPress = {() => submitRegister(email, password)}
                    title="SIGN UP"
                />
            </View>
      </View>
    );
};


const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
    },
    buttonContainer: {
        margin: 10
    },
    input: {
        padding: 10,
        margin: 10,
        height: 40,
        borderWidth: 1,
    },
});

export default RegisterScreen;