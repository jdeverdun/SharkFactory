%
%Calculate x,y overlap between stacks
%@@image:Image1@@image:Image2@@value:nbimage@@value:pas@@string:orientation@@
%@@value:deplacement@@matrix:ecart@@
function [ varargout] = my_calc_decalage( varargin)
%% A partir d'un stack d'image en niveau de gris un déplacement
% est calculé.
% input : 
%               - Image1 et 2 : matrice 3D : 1 de référence, 2: à modifier
%               - nbimage : nombre d'images à utiliser pour évaluer la
%               superposition
%               - pas : la zone dans laquelle chercher la superposition
%               - orientation : 'vd' 'vu' ou 'hf' 'hd' (vertical up-down - horizontal left - right) dans
%               lequel évaluer le décalage
% output : 
%               - deplacement : décalage à appliquer en x ou y 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Number_of_images=size(Image1,3);
listref=[ceil((Number_of_images/2)-floor(nbimage/2)):1:ceil((Number_of_images/2)+floor((nbimage-0.5)/2))]
deplacement=0;
ecart=[];

for pair=listref
    pair
    ecart=[];
    I_ref=uint8(Image1(:,:,pair));
    I_mod=Image2(:,:,pair);
    I_ref=double(I_ref);
    I_mod=double(I_mod);
    difmin=Inf;
    if strcmp(orientation,'hr')
        for i=30:pas
            dif=mean(mean(abs(I_ref(:,end-i+1:end)-I_mod(:,1:i))));
            ecart=[ecart;[i dif]];
        end
    elseif strcmp(orientation,'hl')
        for i=30:pas
            dif=mean(mean(abs(I_ref(:,1:i)-I_mod(:,end-i+1:end))));
            ecart=[ecart;[i dif]];
        end
    elseif strcmp(orientation,'vd')
        for i=30:pas
            dif=mean(mean(abs(I_ref(end-i+1:end,:)-I_mod(1:i,:))));
            ecart=[ecart;[i dif]];
        end
        
    elseif strcmp(orientation,'vu')
        for i=30:pas
            dif=mean(mean(abs(I_ref(1:i,:)-I_mod(end-i+1:end,:))));
            ecart=[ecart;[i dif]];
        end
    end
%     b=spline(ecart(:,1),ecart(:,2));
%     p_der=fnder(b,1);
%     y_prime=ppval(p_der,ecart(:,1));
    deplacement=deplacement+ecart(find(ecart(:,2)==min(ecart(:,2))),1);%find(y_prime==(max(y_prime)));
end
deplacement=deplacement/max(size(listref));
varargout{1}=deplacement;
varargout{2}=ecart;
end

