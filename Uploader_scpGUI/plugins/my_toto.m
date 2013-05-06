%
%
%
%
function [ varargout ] = my_toto( varargin )
%MY_TOTO Summary of this function goes here
%   Detailed explanation goes here
    disp('je suis dans toto là');
	varargout{1}=graythresh(varargin{1});

end

