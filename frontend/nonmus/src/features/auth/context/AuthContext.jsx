import { createContext, useState } from "react";

export const AuthContext = createContext({
  email: "",
  setEmail: () => {},
  name: "",
  setName: () => {},
});

export const AuthProvider = ({ children }) => {
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");

  return (
    <AuthContext.Provider value={{ email, setEmail, name, setName }}>
      {children}
    </AuthContext.Provider>
  );
};
