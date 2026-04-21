import axios from "axios";

const http = axios.create({
  baseURL: "/",
  timeout: 15000
});

http.interceptors.response.use(
  (res) => {
    const body = res.data;
    if (body && body.success === false) {
      return Promise.reject(new Error(body.message || "请求失败"));
    }
    return body;
  },
  (err) => Promise.reject(err)
);

export function withRole(role = "OPERATOR") {
  return { headers: { "X-Role": role } };
}

export default http;
