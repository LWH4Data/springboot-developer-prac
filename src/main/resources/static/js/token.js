const token = searchParam('token')

// 파라미터로 받은 토큰이 있다면
if (token) {
  // 로컬 스토리지에 저장한다.
  localStorage.setItem("access_token", token)
}

// 토큰 값을 찾기위한 메서드.
function searchParam(key) {
  return new URLSearchParams(location.search).get(key);
}