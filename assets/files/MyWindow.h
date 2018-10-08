#include "stdafx.h"
#include "MyControls.h"
#include "MyPwrMng.h"
#include "MyUtil.h"
#include "resource.h"

struct MyWindow {
	TResult *pResult;

	HINSTANCE hInst;								// 현재 인스턴스입니다.
	HWND hWnd;
	
	TCHAR szTitle[MAX_LOADSTRING];					// 제목 표시줄 텍스트입니다.
	TCHAR szWindowClass[MAX_LOADSTRING];			// 기본 창 클래스 이름입니다.	
	TCHAR szText[MAX_LOADSTRING*STRING_COUNT];				// 버튼 텍스트입니다.

	int iaTextLen[STRING_COUNT];

	bool isVistaOrLater;	// Vista이전인지 아닌지(vista이전:*****, vista이상:*****7)

	MyButton *pButtons[BUTTON_COUNT];
	MyStatic *pMessages[STATIC_COUNT];

	MyPwrMng *pMyPwrMng;


	int windowWidth, windowHeight;
	int itemWidth;
	int itemHeight;
	int gapHeight;

	POINTS clickPoint;

	MSG prevLMouseDownMsg;
	MSG prevMouseMoveMsg;
	MSG curLMouseDownMsg;
	MSG curMouseMoveMsg;

};


TResult *MyWindow_GetResult(MyWindow *_this);
MyWindow * MyWindow_CreateMyWindow(HINSTANCE hInstance, int nCmdShow);
void MyWindow_DestroyMyWindow(MyWindow *_this);

BOOL MyWindow_InitInstance(MyWindow *_this, HINSTANCE, int);


bool MyWindow_Draw(MyWindow *_this, HDC hdc);
bool MyWindow_MouseMove(MyWindow *_this, HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);
bool MyWindow_LButtonDown(MyWindow *_this, HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);
bool MyWindow_LButtonUp(MyWindow *_this, HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);


void MyWindow_InitControls(MyWindow *_this);

int MyWindow_GetTextLength(PTCHAR pText);



LRESULT CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);
INT_PTR CALLBACK About(HWND, UINT, WPARAM, LPARAM);


