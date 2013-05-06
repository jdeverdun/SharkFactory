%
%Calculate overlap between 2 Z-stacks
%@@file:path1@@file:path2@@
%@@value:zoverlap@@
function varargout = my_Zcombine(varargin)
    pathimage=varargin{1};
    pathresult=varargin{2};
    iref=my_open_image(strcat(pathimage,'/T1_I1_chan1_stack_rgb.tif'));
    imov=my_open_image(strcat(pathimage,'/T2_I1_chan1_stack_rgb.tif'));
    itmp=iref(:,:,end);
    for i=1:size(imov,3)
        score(i)=sum(sum(abs(itmp-imov(:,:,i))));
    end
    ind=max(find(score==min(score)));
    varargout{1}=ind;
end

