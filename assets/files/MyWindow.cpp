#include "stdafx.h"
#include "MyWindow.h"

//MyWindow		*MyWindow_pMyHandle;			// 자기 자신을 가리키는 포인터

TResult *MyWindow_GetResult(MyWindow *_this)
{
	return _this->pResult;
}

MyWindow * MyWindow_CreateMyWindow(HINSTANCE hInstance, int nCmdShow)
{
	MyWindow *_this = new MyWindow();
	_this->pResult = Result_CreateResult(false, 0, NULL);

	LoadString(hInstance, IDS_APP_TITLE, _this->szTitle, MAX_LOADSTRING);
	LoadString(hInstance, IDC_POWERMANAGER, _this->szWindowClass, MAX_LOADSTRING);	
	
	LoadString(hInstance, IDS_MONITOROFF, _this->szText, MAX_LOADSTRING);
	LoadString(hInstance, IDS_SLEEP, _this->szText+MAX_LOADSTRING, MAX_LOADSTRING);
	LoadString(hInstance, IDS_HIBERNATE, _this->szText+2*MAX_LOADSTRING, MAX_LOADSTRING);
	LoadString(hInstance, IDS_EXITWINDOWS, _this->szText+3*MAX_LOADSTRING, MAX_LOADSTRING);

	LoadString(hInstance, IDS_MESSAGE1, _this->szText+4*MAX_LOADSTRING, MAX_LOADSTRING);
	LoadString(hInstance, IDS_MESSAGE2, _this->szText+5*MAX_LOADSTRING, MAX_LOADSTRING);
	LoadString(hInstance, IDS_MESSAGE3, _this->szText+6*MAX_LOADSTRING, MAX_LOADSTRING);


	int i;
	for (i=0; i<STRING_COUNT; i++) {
		_this->iaTextLen[i] = MyWindow_GetTextLength(_this->szText+i*MAX_LOADSTRING);
	}

	_this->windowWidth = 640;
	_this->windowHeight = 480;
	_this->itemWidth = 200;
	_this->itemHeight = 30;
	_this->gapHeight = 20;

	if (!MyWindow_InitInstance(_this, hInstance, nCmdShow))
		return NULL;

	return _this;
}

void MyWindow_DestroyMyWindow(MyWindow *_this)
{
	int i;
	if (_this->pResult) Result_DestroyResult(_this->pResult);
	for (i=0; i<STATIC_COUNT; i++)  {
		MyStatic_DestroyMyStatic(_this->pMessages[i]);
	}
	for (i=0; i<BUTTON_COUNT; i++)  {
		MyButton_DestroyMyButton(_this->pButtons[i]);
	}
	MyPwrMng_DestroyMyPwrMng(_this->pMyPwrMng);
}



//
//   함수: InitInstance(HINSTANCE, int)
//
//   목적: 인스턴스 핸들을 저장하고 주 창을 만듭니다.
//
//   설명:
//
//        이 함수를 통해 인스턴스 핸들을 전역 변수에 저장하고
//        주 프로그램 창을 만든 다음 표시합니다.
//
BOOL MyWindow_InitInstance(MyWindow *_this, HINSTANCE hInstance, int nCmdShow)
{
 
   //g_hInst = hInstance;	// 인스턴스 핸들을 전역 변수에 저장합니다.
   _this->hInst = hInstance; 

   _this->isVistaOrLater = MyUtil_IsVistaOrLater();

   int screenWidth, screenHeight;   
   int xPos, yPos;

   screenWidth = GetSystemMetrics(SM_CXSCREEN);
   screenHeight = GetSystemMetrics(SM_CYSCREEN);   

   xPos = screenWidth/2 - _this->windowWidth/2;
   yPos = screenHeight/2 - _this->windowHeight/2;

   _this->hWnd = CreateWindow(_this->szWindowClass, _this->szTitle, WS_OVERLAPPEDWINDOW,
      xPos, yPos, _this->windowWidth, _this->windowHeight, NULL, NULL, hInstance, NULL);
   
   if (_this->hWnd==NULL)
   {
      return FALSE;
   }

   MyWindow_InitControls(_this);
   
   _this->pMyPwrMng = MyPwrMng_CreateMyPwrMng();
   if (_this->pMyPwrMng==NULL) {
	   MessageBox(_this->hWnd, MyPwrMng_GetResult(_this->pMyPwrMng)->pMessage, L"Error", 0);
	   return false;
   }      

   return TRUE;
}


bool MyWindow_Draw(MyWindow *_this, HDC hdc) {
	RECT clientRect;
	int i;
	GetClientRect(_this->hWnd, &clientRect);
	HFONT hfnt, hOldFont; 
	hfnt = (HFONT)GetStockObject(SYSTEM_FONT); 
	if (hOldFont = (HFONT)SelectObject(hdc, hfnt)) 
	{
		for (i=0; i<STATIC_COUNT; i++) {
			if ( MyStatic_IsNull(_this->pMessages[i]) == false ) {
				MyStatic_Draw(_this->pMessages[i], hdc);
			}
		}

		for (i=0; i<BUTTON_COUNT; i++) {
			if ( MyButton_IsNull(_this->pButtons[i]) == false ) {
				MyButton_Draw(_this->pButtons[i], hdc);
			}
		}

		SelectObject(hdc, hOldFont); 			
	}
	return true;
}


bool MyWindow_MouseMove(MyWindow *_this, HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam) {
	if (_this->isVistaOrLater) {
		MyPwrMng_TurnOffDisplay7(_this->pMyPwrMng, 7200, 7200);
	}
	else {
		MyPwrMng_TurnOffDisplay(_this->pMyPwrMng, 7200, 7200);
	}
	return true;
}

bool MyWindow_LButtonDown(MyWindow *_this, HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam) {
	int selectedIndex = BUTTON_COUNT;
	int i;
	_this->clickPoint = MAKEPOINTS(lParam);
	for (i=0; i<BUTTON_COUNT; i++)  {
		if ( MyButton_IsPointIn(_this->pButtons[i], _this->clickPoint) ) {
			selectedIndex = i;
			MyButton_SetClicked(_this->pButtons[i], true);
			InvalidateRect(hWnd, NULL, true);
			break;
		}
	}
	return true;
}

bool MyWindow_LButtonUp(MyWindow *_this, HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam) {
	POINTS point;
	int i;
	int selectedIndex = BUTTON_COUNT;
	point = MAKEPOINTS(lParam);
	for (i=0; i<BUTTON_COUNT; i++)  {
		if ( MyButton_IsPointIn(_this->pButtons[i], point) ) {
			selectedIndex = i;
			MyButton_SetClicked(_this->pButtons[i], false);
			InvalidateRect(hWnd, NULL, true);
			break;
		}
	}

	// 마우스를 캡처함으로써 마우스가 윈도우 밖으로 나가더라도 모니터가 켜질 수 있도록 한다.
	// LButtonDown에서 모니터를 끌 때는 마우스를 캡처해야 할 수도 있지만 LButtonUp에서 모니터를 끄면 캡처를 안 해도 된다.
	if (selectedIndex==0) {
		SetCapture(_this->hWnd);
		SetCursor(LoadCursor(NULL, IDC_CROSS));		// 캡처했다는 표시
	}
	else {
		ReleaseCapture();
		SetCursor(LoadCursor(NULL, IDC_ARROW));		// 원래 상태로 커서를 바꾼다.
	}
	// 버튼을 클릭 안 했으면 그대로 리턴
	if (selectedIndex==BUTTON_COUNT)
		return false;
	// 모니터 끄기
	if (selectedIndex==0) {
		if (_this->isVistaOrLater) {
			MyPwrMng_TurnOffDisplay7(_this->pMyPwrMng, 1, 1);
		}
		else {
			MyPwrMng_TurnOffDisplay(_this->pMyPwrMng, 1, 1);
		}

		TResult *pResult = MyPwrMng_GetResult(_this->pMyPwrMng);
		if (pResult->code!=0) {
			MessageBox(hWnd, pResult->pMessage, L"Error", 0);
		}
		//디버깅 코드 : yPwrMng_GetActiveVideoTimeout(pMyPwrMng, &videoTimeoutAc, &videoTimeoutDc);
	}
	// 절전(Sleep)
	else if (selectedIndex==1) {
		MyPwrMng_SetSuspendState(_this->pMyPwrMng, false,true,false);
	}
	// 절전(Hibernate)
	else if (selectedIndex==2) {
		MyPwrMng_SetSuspendState(_this->pMyPwrMng, true,true,true);
	}
	// 시스템 shutdown
	else if (selectedIndex==3) {
		MyPwrMng_ExitWindows(_this->pMyPwrMng);
	}
	return true;
}


void MyWindow_InitControls(MyWindow *_this) 
{
	int messageY, messageHeight;
	messageY = 30;
	messageHeight = 20;

	_this->pMessages[0] = MyStatic_CreateMyStatic(0, messageY, _this->windowWidth, messageHeight, _this->szText+4*MAX_LOADSTRING, _this->iaTextLen[4]);
	messageY = MyStatic_GetY(_this->pMessages[0]) + MyStatic_GetHeight(_this->pMessages[0]) + _this->gapHeight;
	_this->pMessages[1] = MyStatic_CreateMyStatic(0, messageY, _this->windowWidth, messageHeight, _this->szText+5*MAX_LOADSTRING, _this->iaTextLen[5]);
	messageY = MyStatic_GetY(_this->pMessages[1]) + MyStatic_GetHeight(_this->pMessages[1]) + _this->gapHeight;
	_this->pMessages[2] = MyStatic_CreateMyStatic(0, messageY, _this->windowWidth, messageHeight, _this->szText+6*MAX_LOADSTRING, _this->iaTextLen[6]);
	messageY = MyStatic_GetY(_this->pMessages[2]) + MyStatic_GetHeight(_this->pMessages[2]) + _this->gapHeight;

    int x, y, width, height;
	x = _this->windowWidth/2 - _this->itemWidth/2;
	y = messageY + _this->gapHeight;
	width = _this->itemWidth;
	height = _this->itemHeight;
	_this->pButtons[0] = MyButton_CreateMyButton(x, y, width, height, _this->szText, _this->iaTextLen[0]);

	x = _this->windowWidth/2 - _this->itemWidth/2;
	y = MyButton_GetY(_this->pButtons[0]) + MyButton_GetHeight(_this->pButtons[0]) - 1 + _this->gapHeight;
	_this->pButtons[1] = MyButton_CreateMyButton(x, y, width, height, _this->szText+1*MAX_LOADSTRING, _this->iaTextLen[1]);

	x = _this->windowWidth/2 - _this->itemWidth/2;
	y = MyButton_GetY(_this->pButtons[1]) + MyButton_GetHeight(_this->pButtons[1]) - 1 + _this->gapHeight;
	_this->pButtons[2] = MyButton_CreateMyButton(x, y, width, height, _this->szText+2*MAX_LOADSTRING, _this->iaTextLen[2]);

	x = _this->windowWidth/2 - _this->itemWidth/2;
	y = MyButton_GetY(_this->pButtons[2]) + MyButton_GetHeight(_this->pButtons[2]) - 1 + _this->gapHeight;
	_this->pButtons[3] = MyButton_CreateMyButton(x, y, width, height, _this->szText+3*MAX_LOADSTRING, _this->iaTextLen[3]);
  
}

int MyWindow_GetTextLength(PTCHAR pText) {
	if (pText==NULL) return -1;
	int i;
	for (i=0; i<MAX_LOADSTRING; i++) {
		if (pText[i]==L'\0') {
			return i;
		}
	}
	return i;
}



