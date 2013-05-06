%@main@
%toto
%@@file:toto@@
%@@none
function varargout=my_threshold(varargin)
    a=imread(varargin{1});
	th=graythresh(a);
	ap=a.*uint8(a>th);
	h=figure,imshow(ap,[]);
	drawnow
	varargout{1}='';
end