#include "stdafx.h"
#include "MyControls.h"
#include "MyPwrMng.h"
#include "MyUtil.h"
#include "resource.h"

struct MyWindow {
	TResult *pResult;

	HINSTANCE hInst;								// ���� �ν��Ͻ��Դϴ�.
	HWND hWnd;
	
	TCHAR szTitle[MAX_LOADSTRING];					// ���� ǥ���� �ؽ�Ʈ�Դϴ�.
	TCHAR szWindowClass[MAX_LOADSTRING];			// �⺻ â Ŭ���� �̸��Դϴ�.	
	TCHAR szText[MAX_LOADSTRING*STRING_COUNT];				// ��ư �ؽ�Ʈ�Դϴ�.

	int iaTextLen[STRING_COUNT];

	bool isVistaOrLater;	// Vista�������� �ƴ���(vista����:*****, vista�̻�:*****7)

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


