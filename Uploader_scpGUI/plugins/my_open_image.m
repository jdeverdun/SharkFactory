%
%open rgb image stack and send it back as gray stack
%@@file:imagepath@@
%@@image:stackimage@@
function img=open_image(c)
    i=1;
    c
    while 1
        try
            img(:,:,i)=rgb2gray(imread(c,i));
            i=i+1;
        catch exception
            exception.message
            break;
        end
    end
end