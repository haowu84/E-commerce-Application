import React from 'react';
import { Button, TextInput, StyleSheet, View } from 'react-native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { NavigationContainer } from '@react-navigation/native';
import RegisterScreen from './Register';
import MainScreen from './Main';
import MovieScreen from './Movie';
import Axios from "axios";

async function login(loginRequest) {
    const requestBody = {
        email: loginRequest.email,
        password: loginRequest.password
    };

    const options = {
        method: "POST",  
        baseURL: "http://10.0.2.2:8082",
        url: "/login",  
        data: requestBody  
    }

    return Axios.request(options);
}


const LoginScreen = ({navigation}) => {
    const [email, onChangeEmail] = React.useState(null);
    const [password, onChangePassword] = React.useState(null);

    const submitLogin = (email, password) => {
        const payLoad = {
            email: email,
            password: password.split('')
        }

        login(payLoad)
            .then(response => {
                navigation.navigate("Main", {accessToken: response.data.accessToken, refreshToken: response.data.refreshToken});
            })
            .catch(error => {
                if (error.response.data.result.code == 1002 || error.response.data.result.code == 1003) {
                    alert("Email address is invalid");
                } else if (error.response.data.result.code == 1022) {
                    alert("Password does not match")
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
                    onPress = {() => submitLogin(email, password)}
                    title="LOGIN"
                />
            </View>
            <View style={styles.buttonContainer}>
                <Button
                    onPress = {() => navigation.navigate("Register")}
                    title="SIGN UP"
                />
            </View>
      </View>
    );
};

const Stack = createNativeStackNavigator();

const MyStack = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
            name="Login"
            component={LoginScreen}
            options={{ title: 'FabFlix' }}
        />
        <Stack.Screen name="Register" component={RegisterScreen} />
        <Stack.Screen name="Main" component={MainScreen} />
        <Stack.Screen name="Movie" component={MovieScreen} />
      </Stack.Navigator>
    </NavigationContainer>
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

export default MyStack;