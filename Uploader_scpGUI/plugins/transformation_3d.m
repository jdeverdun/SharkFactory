%@main@
%UI that let you fuse multiple stack manually
%@@none
%@@none
function varargout = transformation_3d(varargin)
% TRANSFORMATION_3D MATLAB code for transformation_3d.fig
%      TRANSFORMATION_3D, by itself, creates a new TRANSFORMATION_3D or raises the existing
%      singleton*.
%
%      H = TRANSFORMATION_3D returns the handle to a new TRANSFORMATION_3D or the handle to
%      the existing singleton*.
%
%      TRANSFORMATION_3D('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in TRANSFORMATION_3D.M with the given input arguments.
%
%      TRANSFORMATION_3D('Property','Value',...) creates a new TRANSFORMATION_3D or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before transformation_3d_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to transformation_3d_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help transformation_3d

% Last Modified by GUIDE v2.5 12-Jul-2011 15:27:18

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @transformation_3d_OpeningFcn, ...
                   'gui_OutputFcn',  @transformation_3d_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before transformation_3d is made visible.
function transformation_3d_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to transformation_3d (see VARARGIN)

% Choose default command line output for transformation_3d
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);
global coupeactif fichactif path listfile listcoupe;
fichactif='';

path='C:\Users\Mobilette\Documents\Stage2010-2011\MATLAB mcode\programme\LH-POMC\test\results';
detecter_Callback(hObject, eventdata, handles);
% UIWAIT makes transformation_3d wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = transformation_3d_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future versiosn of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;



function xrotation_Callback(hObject, eventdata, handles)
% hObject    handle to xrotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of xrotation as text
%        str2double(get(hObject,'String')) returns contents of xrotation as a double


% --- Executes during object creation, after setting all properties.
function xrotation_CreateFcn(hObject, eventdata, handles)
% hObject    handle to xrotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function yrotation_Callback(hObject, eventdata, handles)
% hObject    handle to yrotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of yrotation as text
%        str2double(get(hObject,'String')) returns contents of yrotation as a double


% --- Executes during object creation, after setting all properties.
function yrotation_CreateFcn(hObject, eventdata, handles)
% hObject    handle to yrotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function zrotation_Callback(hObject, eventdata, handles)
% hObject    handle to zrotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of zrotation as text
%        str2double(get(hObject,'String')) returns contents of zrotation as a double


% --- Executes during object creation, after setting all properties.
function zrotation_CreateFcn(hObject, eventdata, handles)
% hObject    handle to zrotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function xtransform_Callback(hObject, eventdata, handles)
% hObject    handle to xtransform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of xtransform as text
%        str2double(get(hObject,'String')) returns contents of xtransform as a double


% --- Executes during object creation, after setting all properties.
function xtransform_CreateFcn(hObject, eventdata, handles)
% hObject    handle to xtransform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function ytransform_Callback(hObject, eventdata, handles)
% hObject    handle to ytransform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ytransform as text
%        str2double(get(hObject,'String')) returns contents of ytransform as a double


% --- Executes during object creation, after setting all properties.
function ytransform_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ytransform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function ztransform_Callback(hObject, eventdata, handles)
% hObject    handle to ztransform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ztransform as text
%        str2double(get(hObject,'String')) returns contents of ztransform as a double


% --- Executes during object creation, after setting all properties.
function ztransform_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ztransform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in listfichiers.
function listfichiers_Callback(hObject, eventdata, handles)
% hObject    handle to listfichiers (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns listfichiers contents as cell array
%        contents{get(hObject,'Value')} returns selected item from listfichiers
global coupeactif fichactif path listfile listcoupe;
list=dir(path);
count=1;
listcoupe={};
try
    fichactif=listfile{get(handles.listfichiers,'Value')};
    for i=3:max(size(list))
        if ~isempty(regexpi(list(i).name,strcat(fichactif(1:6),'.+',fichactif(end))))
            listcoupe{count}=list(i).name(1:end-4);
            count=count+1;
        end
    end
    set(handles.listescoupes,'String',listcoupe);
    try
        coupeactif=listcoupe{get(handles.listescoupes,'Value')};
    catch exception
    end
catch exception
end

% --- Executes during object creation, after setting all properties.
function listfichiers_CreateFcn(hObject, eventdata, handles)
% hObject    handle to listfichiers (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in listescoupes.
function listescoupes_Callback(hObject, eventdata, handles)
% hObject    handle to listescoupes (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns listescoupes contents as cell array
%        contents{get(hObject,'Value')} returns selected item from listescoupes
global coupeactif listcoupe;
coupeactif=listcoupe{get(handles.listescoupes,'Value')};

% --- Executes during object creation, after setting all properties.
function listescoupes_CreateFcn(hObject, eventdata, handles)
% hObject    handle to listescoupes (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in valid_transform.
function valid_transform_Callback(hObject, eventdata, handles)
% hObject    handle to valid_transform (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global coupeactif fichactif path
cd(path)
load(coupeactif);
c1=centroids3d;
c1(:,1)=round(c1(:,1)+str2double(get(handles.xtransform,'String')));
c1(:,2)=round(c1(:,2)+str2double(get(handles.ztransform,'String')));
save(coupeactif,'centroids3d','c1');



% --- Executes on button press in Reset.
function Reset_Callback(hObject, eventdata, handles)
% hObject    handle to Reset (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.xtransform,'String',0);
set(handles.ytransform,'String',0);
set(handles.ztransform,'String',0);
set(handles.xrotation,'String',0);
set(handles.yrotation,'String',0);
set(handles.zrotation,'String',0);


% --- Executes on button press in detecter.
function detecter_Callback(hObject, eventdata, handles)
% hObject    handle to detecter (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global coupeactif fichactif path listfile listcoupe;
list=dir(path);
listfile={};
count=1;
nomconnu='';
nomcourt='';
% on récupère la liste des fichiers
for i=3:max(size(list))
    elem=cell2mat(regexp(list(i).name, '^[0-9]*', 'match'));
    if ~isempty(elem)
        nomcourt=strcat(num2str(elem),'_@@',list(i).name(end-4));
        if max(size(nomcourt))>1 && isempty(regexpi(nomconnu,nomcourt))
            listfile{count}=nomcourt;
            count=count+1;
            nomconnu=strcat(nomconnu,'_',nomcourt)
        end
    end
end
set(handles.listfichiers,'String',listfile);
listfichiers_Callback(hObject, eventdata, handles);


% --- Executes on button press in recoller.
function recoller_Callback(hObject, eventdata, handles)
% hObject    handle to recoller (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global listcoupe fichactif
cent=[];
for i=1:max(size(listcoupe))
    load(listcoupe{i});
    try
        cent=[cent;c1];
    catch exception
        error('Translation des centroides sur cette coupe non réalisé.');
        break;
    end
end
xmax=max(cent(:,2));
ymax=max(cent(:,1));
image=zeros(xmax,ymax,max(cent(:,3)));
for i=1:max(size(cent))
    if ~isnan(cent(i,:))
        image(cent(i,2),cent(i,1),cent(i,3))=1;
    end; 
end
save(fichactif,'image');


% --- Executes on button press in pushbutton5.
function pushbutton5_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global path
path = uigetdir('.','Select the directory');