%@main
%test function to see if java2matlab is working
%@@value:nb@@
%@@value:s1@@value:s2@@
function [varargout]=test(varargin)
	nb=varargin{1};
	[image1 image2]=my_super_fun(nb,2);
	figure,imshow(image1,[]),colormap jet
	varargout{1}=10;
	varargout{2}=20;
end