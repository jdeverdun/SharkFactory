%
% toto
%
%
function varargout=my_super_fun(varargin)
    a=rand(varargin{1},varargin{1})*100;
	a=a./50;
	b=my_toto(a);
	varargout{1}=b.*a;
	varargout{2}=my_seuil(a,b);
end