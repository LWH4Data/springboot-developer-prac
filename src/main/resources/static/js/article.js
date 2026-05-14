// API를 호출해 섬네일 AI 제안을 받는 기능==================================================
// id가 ai-thumbnail-btn인 엘리먼트.
const aiThumbnailButton = document.getElementById('ai-thumbnail-btn');
if (aiThumbnailButton) {
  aiThumbnailButton.addEventListener('click', async () => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    // 제목, 내용이 비어있으면 경고창 띄우기.
    if (!title.trim() && !content.trim()) {
      alert('제목이나 내용을 먼저 입력해 주세요.');
      return;
    }

    // 로딩 모달 표시.
    const loadingDiv = document.getElementById('ai-thumbnail-loading');
    loadingDiv.style.display = 'block';
    aiThumbnailButton.disabled = true;

    // AI 섬네일 생성 API 호출.
    fetch('/api/ai-thumbnails', {
      method: 'POST',
      body: JSON.stringify({
        title: title,
        content: content
      }),
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('access_token'),
        'Content-Type': 'application/json',
      },
    }).then((response) => {
      if (!response.ok) {
        alert('섬네일 생성에 실패했습니다.');
        throw new Error();
      }
      return response.json();
    }).then((data) => {
      // 이미지 URL을 이미지 URL 입력란에 설정하고 미리보기 표시.
      document.getElementById('image-url').value = data.imageUrl;
      displayImagePreview(data.imageUrl);
    }).finally(() => {
      loadingDiv.style.display='none';
      aiThumbnailButton.disabled = false;
    });
  });
}

// 삭제 기능.============================================================================
// HTML에서 id를 delete-btn으로 설정한 엘리먼트를 찾는다.
const deleteButton = document.getElementById('delete-btn');

// 토큰 기반으로 수정.
if (deleteButton) {
  deleteButton.addEventListener("click", (event) => {
    let id = document.getElementById("article-id").value;
    function success() {
      alert("삭제가 완료되었습니다.");
      location.replace("/articles");
    }

    httpRequest("DELETE", "/api/articles/" + id, null, success, fail);
  });
}

// // delete-btn이 있다면
// if (deleteButton) {
//   // 클릭 이벤트가 발생하면 fetch() 메소드를 통해 "/api/articles/ DELETE" 요청을 보낸다.
//   deleteButton.addEventListener('click', event => {
//     let id = document.getElementById('article-id').value;
//     fetch(`/api/articles/${id}`, {
//       method: 'DELETE'
//     })
//       // fetch()가 잘 완료된 경우 then() 메소드를 실행한다.
//       .then(() => {
//         // alert()는 then() 메소드가 실행되는 시점에 웹 브라우저 화면으로 삭제가 완료되었음을 알리는 팝업을 띄운다.
//         alert('삭제가 완료되었습니다.');
//         // location.replace(): 실행 시 사용자의 웹 브라우저 화면을 현재 주소를 기반해 옮겨준다.
//         location.replace('/articles');
//     });
//   });
// }

// 수정 기능.====================================================================
// id가 modify-btn인 엘리먼트 조회
const modifyButton = document.getElementById('modify-btn')

// 토큰 기반으로 수정.
if(modifyButton) {
  modifyButton.addEventListener("click", (event) => {
    let params = new URLSearchParams(location.search);
    let id = params.get("id");

    const body = JSON.stringify({
      title: document.getElementById('title').value,
      imageUrl: document.getElementById('image-url').value,
      content: document.getElementById('content').value
    })

    function success() {
      alert("수정 완료되었습니다.");
      location.replace("/articles/" + id);
    }

    httpRequest("PUT", "/api/articles/" + id, body, success, fail);
  });
}

// if (modifyButton) {
//   // 클릭 이벤트가 발생하면 수정 API 요청.
//   modifyButton.addEventListener('click', event => {
//     let params = new URLSearchParams(location.search);
//     let id  = params.get('id');
//
//     // fetch() 메소드를 활용해 수정 API로 "/api/articles/ PUT" 요청을 보냄.
//     fetch(`/api/articles/${id}`, {
//       method: 'PUT',
//       // headers에 요청 형식을 지정함.
//       headers: {
//         "Content-Type": "application/json",
//       },
//       // body에는 HTML에 입력한 데이터를 JSON 형식으로 바꾸어 보냄.
//       body: JSON.stringify({
//         title: document.getElementById('title').value,
//         content: document.getElementById('content').value,
//         imageUrl: document.getElementById('image-url')?.value || ''
//       })
//     })
//     // 요청이 완료되면 then() 메소드로 작업 마무리.
//     .then(() => {
//       alert('수정이 완료되었습니다.');
//       location.replace(`/articles/${id}`);
//     });
//   });
// }

// 등록 기능 추가.==================================================================
// id가 create-btn인 엘리먼트.
const createButton = document.getElementById("create-btn")

if (createButton) {
  // 토큰 기반으로 수정.
  // 등록 버튼을 클릭하면 /api/articles로 요청을 보냄.
  createButton.addEventListener("click", (event) => {
    const body = JSON.stringify({
      title: document.getElementById('title').value,
      imageUrl: document.getElementById('image-url').value,
      content: document.getElementById('content').value
    })
    function success() {
      alert("등록 완료되었습니다.");
      location.replace("/articles");
    }
    const onCreateFail = () => {
      alert("등록 실패했습니다.");
      location.replace("/articles");
    };

    httpRequest("POST", "/api/articles", body, success, onCreateFail);
  });
}

// 쿠키를 가져오는 함수.
function getCookie(key) {
  var result = null;
  var cookie = document.cookie.split(";");
  cookie.some(function (item) {
    item = item.replace(" ", "");

    var dic = item.split("=");

    if (key === dic[0]) {
      result = dic[1];
      return true;
    }
  });
  return result;
}

// 공통 실패 콜백.
function fail() {
  alert("요청 처리 중 오류가 발생했습니다.");
}

// HTTP 요청을 보내는 함수.
function httpRequest(method, url, body, success, fail) {
  fetch(url, {
    method: method,
    headers: {
      // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가.
      Authorization: "Bearer " + localStorage.getItem("access_token"),
      "Content-Type": "application/json",
    },
    body: body,
  }).then((response) => {
    if (response.status === 200 || response.status === 201) {
      return success();
    }
    const refresh_token = getCookie("refresh_token");
    if (response.status === 401 && refresh_token) {
      fetch("/api/token", {
        method: "POST",
        credentials: "same-origin",
        headers: {
          Authorization: "Bearer " + localStorage.getItem("access_token"),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          refreshToken: refresh_token,
        }),
      })
        .then((res) => {
          if (!res.ok) {
            throw new Error("Failed to refresh access token: " + res.status);
          }
          return res.json();
        })
        .then((result) => {
          if (!result || !result.accessToken) {
            throw new Error("Invalid refresh response");
          }
          // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체.
          localStorage.setItem("access_token", result.accessToken);
          httpRequest(method, url, body, success, fail);
        })
        .catch((error) => {
          console.error("token refresh failed", error);
          fail();
        });
    } else {
      return fail();
    }
  });
}
//   // 클릭 이벤트가 감지되면 생성 API 요청.
//   createButton.addEventListener("click", (event) => {
//     fetch("/api/articles", {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       body: JSON.stringify({
//         title: document.getElementById("title").value,
//         content: document.getElementById("content").value,
//         imageUrl: document.getElementById('image-url').value
//       }),
//     }).then(() => {
//       alert("등록 완료되었습니다.");
//       location.replace("/articles");
//     });
//   });
// }

// AI 도움받기 버튼 작동 추가.============================================================
// id가 ai-assist-btn인 엘리먼트.
const aiAssistButton = document.getElementById('ai-assist-btn');

if (aiAssistButton) {
  // 클릭 이벤트가 감지되면 모달을 열고 이전 제안 숨기기
  aiAssistButton.addEventListener('click', event => {
    $('#aiAssistModal').modal('show');
    document.getElementById('ai-suggestion').style.display='none';
    document.getElementById('ai-question').value='';
  });
}

// id가 get-suggestion-btn인 일리먼트 (모달에 있느 버튼)
const getSuggestionButton = document.getElementById('get-suggestion-btn');

if (getSuggestionButton) {
  // 클릭 이벤트가 감지되면 작성한 내용을 바탕으로 글 작성 도움 API 호출.
  getSuggestionButton.addEventListener('click', event => {
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const question = document.getElementById('ai-question').value;

    if (!question.trim()) {
      alert('고민되는 내용을 입력해주세요.');
      return;
    }

    document.getElementById('ai-loading').style.display='block';
    document.getElementById('ai-suggestion').style.display='none';

    const body = JSON.stringify({
      title: title,
      content: content,
      question: question
    });

    fetch('/api/ai-suggestions', {
      method: 'POST',
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('access_token'),
        'Content-Type': 'application/json',
      },
      body: body
    })
      .then(response => {
        return response.json();
      })
      .then(data => {
        document.getElementById('ai-loading').style.display='none';
        const suggestionContent=document.getElementById('ai-suggestion-content');

        // 응답이 오면 제안 뷰 표시.
        let html = '';
        if (data.suggestions && data.suggestions.length > 0) {
          html += '<ul class="list-group">';
          data.suggestions.forEach((suggestion, index) => {
            html += `<li class="list-group-item suggestion-item"
            style="cursor: pointer;" data-suggestion="${suggestion.replace(/"/g, '&quot;')}"
            title="클릭하면 본문에 추가됩니다">
              ${suggestion}
              <small class="text-muted float-right">클릭하여 추가</small>
                </li>`;
          });
          html += '</ul>';
        }

        suggestionContent.innerHTML = html;
        document.getElementById('ai-suggestion').style.display = 'block';
      })
  });
}

// 제안 선택을 누르면 현재 내용 끝에 제안을 추가.
const suggestionContent = document.getElementById('ai-suggestion-content');
if (suggestionContent) {
  suggestionContent.addEventListener('click', function(e) {
    const suggestionItem = e.target.closest('.suggestion-item');
    if (suggestionItem) {
      const suggestion = suggestionItem.getAttribute('data-suggestion');
      const contentTextarea = document.getElementById('content');

      const currentContent = contentTextarea.value;
      const separator = currentContent && !currentContent.endsWith('\n') ? '\n\n' : '';
      contentTextarea.value = currentContent + separator + suggestion;
      $('#aiAssistModal').modal('hide');
      contentTextarea.focus();
    }
  });
}

// 이미지 업로드와 관련된 코드 추가.=======================================================
// 페이지 로드 시 기존 이미지 표시
window.addEventListener('DOMContentLoaded', () => {
  // html에 imageUrl 정보를 받음.
  const imageUrl = document.getElementById('image-url')?.value;
  // imageUrl이 있다면
  if (imageUrl) {
    // 기존에 저장된 이미지 URL이 있으면 미리보기 영역에 표시한다
    displayImagePreview(imageUrl);
  }
});

// id가 image-upload인 엘리먼트.
// image-upload를 받아 온다.
const imageUpload = document.getElementById('image-upload');
// 만약 imageUpload라면
if (imageUpload) {
  // 클릭 이벤트가 감지되면 이밎 업로드 요청 처리.
  imageUpload.addEventListener('change', async (event) => {
    // 사용자가 선택한 파일 목록에서 첫 번째 파일을 가져와 file 변수에 저장한다.
    const file = event.target.files[0];
    // file이 없다면 return한다.
    if (!file) return;

    // 파일의 메타 데이터를 검증하고 이미지가 아니라면 아래와 같이 반환한다.
    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드 가능합니다.');
      return;
    }

    // 이미지 업로드 요청.
    const formData = new FormData();
    formData.append('file', file);

    // 서버에 이미지 업로드 요청을 보낸다.
    fetch('/api/upload', {
      method: 'POST',
      body: formData,
      headers: {
        Authorization: 'Bearer ' + localStorage.getItem('access_token'),
      },
    }).then((response) => {
      // 서버의 응답이 ok가 아니라면 실패하였음을 알린다.
      if (!response.ok) {
        alert('이미지 업로드에 실패했습니다.');
        throw new Error();
      }
      // 서버의 응답이 ok라면 json을 반환한다.
      return response.json();
    })
      .then((data) => {
        // 서버가 반환한 이미지 URL을 hidden input에 저장한다.
        document.getElementById('image-url').value = data.imageUrl;
        // 저장된 이미지 URL을 이용해 미리보기를 화면에 표시한다.
        displayImagePreview(data.imageUrl);
      })
      // 업로드 요청 또는 응답 처리 중 발생한 에러를 콘솔에 출력한다.
      .catch((e) => console.error(e));
  })
}

// 이미지를 미리보기 위한 함수.
function displayImagePreview(imageUrl) {
  // 미리보기 전체 영역을 저장한다.
  const preview = document.getElementById('image-preview');
  // 미리보기 전체 영역 중 실제 이미지를 저장한다.
  const previewImg = document.getElementById('preview-img');

  // 미리보기 영역, 미리보기 이미지, 이미지 url이 전부 있는 경우
  if (preview && previewImg && imageUrl) {
    // img 태그의 src에 이미지 URL을 넣어 실제 이미지를 표시한다.
    previewImg.src = imageUrl;
    // 숨겨져 있던 미리보기 영역을 화면에 보이게 한다.
    preview.style.display = 'block';
  }
}

// id가 remove-image-btn인 엘리먼트.
const removeImageButton = document.getElementById('remove-image-btn');
if(removeImageButton) {
  // 클릭 이벤트가 감지되면 이미지 제거 처리.
  removeImageButton.addEventListener('click', ()=> {
    // 각 값을 비우고 스타일을 지운다.
    document.getElementById('image-url').value = '';
    document.getElementById('image-upload').value = '';
    document.getElementById('image-preview').style.display = 'none';
  })
}

// 로그아웃 기능.
const logoutButton = document.getElementById('logout-btn');

if (logoutButton) {
  logoutButton.addEventListener('click', event => {
    fetch('/api/refresh-token', {
      method: 'DELETE',
      credentials: 'same-origin',
    })
      .finally(() => {
        // 로그아웃 시 클라이언트 액세스 토큰은 항상 정리한다.
        localStorage.removeItem('access_token');
        location.replace('/login');
      });
  });
}