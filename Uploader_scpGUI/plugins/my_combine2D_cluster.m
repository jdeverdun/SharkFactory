%
%Combine stack (2D)
%@@file:chemin_csv@@file:chemin_im@@value:tranche@@@@matrix:mat@@
%@@matrice:pix_total@@matrix:pix_total_gfp@@
function [pix_total pix_total_gfp] = my_combine2D_cluster(varargin)

DEBUG=0;
chemin_csv=varargin{1};
chemin_im=varargin{2};
tranche=varargin{3};
ref=varargin{4};
mat=varargin{5};
tranche=num2str(tranche);
%cd(chemin);
iref=(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I1_chan1_stack_rgb.tif')));
taille_r=size(iref,1);
taille_c=size(iref,2);
% initialise les d�calages
toleft=0;
toright=0;
toup=0;
todown=0;
%%  TEST uniquement
if DEBUG
    oref=iref;
    seuil=20;%graythresh(iref);
    iref=iref>seuil;
    obj=bwconncomp(iref);
    pix_total=regionprops(obj,'PixelList');
    pix_total=cat(1,pix_total.PixelList);
end
%% parcours en spirale de la matrice


% Construction de matrice d'incr�ments (ligne,colonne) pour
% le parcours en spirale
idx = [];
s = 1;
for n=1:size(mat,1)
   
    s = s*-1;
    
    temp = repmat([s 0],n,1);
    temp2 = repmat([0 -s],n,1);
    
    idx = [idx;temp(:) temp2(:)];
    
end

% Trac� de l'image pour controle
if DEBUG
    im_aff=mat;
    figure
    im = imagesc(mat);
    axis image off

    hold on
end
% Point de d�part
cc = ref(2);
rc = ref(1);

c = cc;
r = rc;
ind_pos=2;
% matrice d'indice
mat_ind=zeros(size(mat));
for n=1:size(idx,1)
    
    c = c+idx(n,2);
    r = r+idx(n,1); 
    
    % Test pour savoir si le pixel courant n'est pas en dehors de l'image
    if c<1 || c>size(mat,1) || r<1 || r>size(mat,2)
        break
    end
    % Modification de la valeur du pixel courant
    mat_ind(r,c)=n+1;
end
% premier round 
c = cc;
r = rc;
for n=1:size(idx,1)
    
    c = c+idx(n,2);
    r = r+idx(n,1); 
    % Test pour savoir si le pixel courant n'est pas en dehors de l'image
    if c<1 || c>size(mat,1) || r<1 || r>size(mat,2) || (toup~=0 && todown ~=0 && toleft~=0 && toright~=0)
        break
    end
    % Modification de la valeur du pixel courant
    if mat(r,c)==1
        r
        c
        if n>1 
            cp=c-idx(n-1,2); % c previous
            rp=r-idx(n-1,1);
        end;
        iactu=(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I',num2str(ind_pos),'_chan1_stack_rgb.tif')));
        if r<rc && c==cc
            if toup==0
                [toup e]=my_calc_decalage(iref,iactu,5,200,'vu');
                toup=round(toup);
                todown=toup;
            end
        elseif r>rc && c==cc
            if todown==0
                [todown e]=my_calc_decalage(iref,iactu,5,200,'vd');
                todown=round(todown);
                toup=todown;
            end
        elseif r==rc && c<cc
            if toleft==0
                [toleft e]=my_calc_decalage(iref,iactu,5,200,'hl');
                toleft=round(toleft);
                toright=toleft;
            end
        elseif r==rc && c>cc
            if toright==0
                [toright e]=my_calc_decalage(iref,iactu,5,200,'hr');
                toright=round(toright);
                toleft=toright;
            end
        end
    end
    ind_pos=ind_pos+1;
end

%%
ind_pos=2;
if toleft==0 ||toup==0
    c = cc;
    r = rc;
    for n=1:size(idx,1)

        c = c+idx(n,2);
        r = r+idx(n,1); 
        % Test pour savoir si le pixel courant n'est pas en dehors de l'image
        if c<1 || c>size(mat,1) || r<1 || r>size(mat,2) || (toup~=0 && todown ~=0 && toleft~=0 && toright~=0)
            break
        end
        % Modification de la valeur du pixel courant
        if mat(r,c)==1
            r
            c
            if n>1 
                cp=c-idx(n-1,2); % c previous
                rp=r-idx(n-1,1);
            end;
            iactu=(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I',num2str(ind_pos),'_chan1_stack_rgb.tif')));
            if r>rc && c<cc && toleft==0
                if mat(rp,cp)==1 
                    [toleft e]=my_calc_decalage(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I',num2str(ind_pos-1),'_chan1_stack_rgb.tif')),iactu,5,200,'hl');
                    toleft=round(toleft);
                    toright=toleft;
                end
            elseif r<rc && c>cc && toright==0
                if mat(rp,cp)==1
                    [toright e]=my_calc_decalage(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I',num2str(ind_pos-1),'_chan1_stack_rgb.tif')),iactu,5,200,'hr');
                    toright=round(toright);
                    toleft=toright;
                end
            end
            if r<rc && toup==0
                if mat(rp+1,cp)==1
                    [toup e]=my_calc_decalage(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I',num2str(mat_ind(rp+1,cp)),'_chan1_stack_rgb.tif')),iactu,5,200,'vu');
                    toup=round(toup);
                    todown=todown;
                end
            elseif r>rc && todown==0
                if mat(rp-1,cp)==1
                    [todown e]=my_calc_decalage(my_open_image(strcat(chemin_im,'/T',num2str(tranche),'_I',num2str(mat_ind(rp-1,cp)),'_chan1_stack_rgb.tif')),iactu,5,200,'vd');
                    todown=round(todown);
                    toup=todown;
                end
            end
        end
        ind_pos=ind_pos+1;
    end
end


%%
toleft
toright
toup
todown
c = cc;
r = rc;
ind_pos=2;
% Parcours en spirale
pix_total_unknow=nan(1,3);
pix_total_gfp=nan(1,3);
pix_total=nan(1,3);
try
    pix_total=csvread(strcat(chemin_csv,'/T',num2str(tranche),'_I1_chan1_stack_centroids_non_gfp.csv'));
catch exception
    exception.message;
end
try
    pix_total_gfp=csvread(strcat(chemin_csv,'/T',num2str(tranche),'_I1_chan1_stack_centroids_gfp.csv'));
catch exception
    exception.message;
end
try
    pix_total_unknow=csvread(strcat(chemin_csv,'/T',num2str(tranche),'_I1_chan1_stack_centroids_unknow.csv'));
catch exception
    exception.message;
end
for n=1:size(idx,1)
    
    c = c+idx(n,2);
    r = r+idx(n,1); 
    
    % Test pour savoir si le pixel courant n'est pas en dehors de l'image
    if c<1 || c>size(mat,1) || r<1 || r>size(mat,2)
        break
    end
    % Modification de la valeur du pixel courant
    if mat(r,c)==1 
        if DEBUG
            iactu=(my_open_image(strcat(chemin,'/T',num2str(tranche),'_I',num2str(ind_pos),'/T',tranche,'_I',num2str(ind_pos),'_z11c1.tif')));
            iactu=iactu>seuil;
            obj=bwconncomp(iactu);
            pix_actu=regionprops(obj,'PixelList');
            pix_actu=cat(1,pix_actu.PixelList);
            size(pix_actu,1)
        else
            haveNONGFP=1;
            haveGFP=1;
            haveUNKNOW=1;
            try
                pix_actu=csvread(strcat(chemin_csv,'/T',num2str(tranche),'_I',num2str(ind_pos),'_chan1_stack_centroids_non_gfp.csv'));
            catch exception
                haveNONGFP=0;
                exception.message
            end
            try
                pix_actu_gfp=csvread(strcat(chemin_csv,'/T',num2str(tranche),'_I',num2str(ind_pos),'_chan1_stack_centroids_gfp.csv'));
            catch exception
                haveGFP=0;
                exception.message
            end
            try
                pix_actu_unknow=csvread(strcat(chemin_csv,'/T',num2str(tranche),'_I',num2str(ind_pos),'_chan1_stack_centroids_unknow.csv'));
            catch exception
                haveUNKNOW=0;
                exception.message
            end
        end
        % RAJOUTER LA 3ieme DIM
        if r<rc && c==cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,2)<taille_r-(toup)
                        pix_total(size(pix_total,1)+1,2)=pix_actu(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total(size(pix_total,1),1:2:end)=pix_actu(p,1:2:end);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,2)<taille_r-(toup)
                        pix_total_gfp(size(pix_total_gfp,1)+1,2)=pix_actu_gfp(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total_gfp(size(pix_total_gfp,1),1:2:end)=pix_actu_gfp(p,1:2:end);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,2)<taille_r-(toup)
                        pix_total_unknow(size(pix_total_unknow,1)+1,2)=pix_actu_unknow(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total_unknow(size(pix_total_unknow,1),1:2:end)=pix_actu_unknow(p,1:2:end);
                    end
                end
            end
        elseif r>rc && c==cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,2)>(todown)
                        pix_total(size(pix_total,1)+1,2)=pix_actu(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total(size(pix_total,1),1:2:end)=pix_actu(p,1:2:end);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,2)>(todown)
                        pix_total_gfp(size(pix_total_gfp,1)+1,2)=pix_actu_gfp(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total_gfp(size(pix_total_gfp,1),1:2:end)=pix_actu_gfp(p,1:2:end);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,2)>(todown)
                        pix_total_unknow(size(pix_total_unknow,1)+1,2)=pix_actu_unknow(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total_unknow(size(pix_total_unknow,1),1:2:end)=pix_actu_unknow(p,1:2:end);
                    end
                end
            end
        elseif r==rc && c<cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,1)<taille_c-(toleft)
                        pix_total(size(pix_total,1)+1,1)=pix_actu(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total(size(pix_total,1),2:end)=pix_actu(p,2:end);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,1)<taille_c-(toleft)
                        pix_total_gfp(size(pix_total_gfp,1)+1,1)=pix_actu_gfp(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total_gfp(size(pix_total_gfp,1),2:end)=pix_actu_gfp(p,2:end);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,1)<taille_c-(toleft)
                        pix_total_unknow(size(pix_total_unknow,1)+1,1)=pix_actu_unknow(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total_unknow(size(pix_total_unknow,1),2:end)=pix_actu_unknow(p,2:end);
                    end
                end
            end
        elseif r==rc && c>cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,1)>(toright)
                        pix_total(size(pix_total,1)+1,1)=pix_actu(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total(size(pix_total,1),2:end)=pix_actu(p,2:end);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,1)>(toright)
                        pix_total_gfp(size(pix_total_gfp,1)+1,1)=pix_actu_gfp(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total_gfp(size(pix_total_gfp,1),2:end)=pix_actu_gfp(p,2:end);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,1)>(toright)
                        pix_total_unknow(size(pix_total_unknow,1)+1,1)=pix_actu_unknow(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total_unknow(size(pix_total_unknow,1),2:end)=pix_actu_unknow(p,2:end);
                    end
                end
            end
        elseif r>rc && c>cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,1)>(toright) && pix_actu(p,2)>(todown)
                        pix_total(size(pix_total,1)+1,1)=pix_actu(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total(size(pix_total,1),2)=pix_actu(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total(size(pix_total,1),3)=pix_actu(p,3);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,1)>(toright) && pix_actu_gfp(p,2)>(todown)
                        pix_total_gfp(size(pix_total_gfp,1)+1,1)=pix_actu_gfp(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total_gfp(size(pix_total_gfp,1),2)=pix_actu_gfp(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total_gfp(size(pix_total_gfp,1),3)=pix_actu_gfp(p,3);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,1)>(toright) && pix_actu_unknow(p,2)>(todown)
                        pix_total_unknow(size(pix_total_unknow,1)+1,1)=pix_actu_unknow(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total_unknow(size(pix_total_unknow,1),2)=pix_actu_unknow(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total_unknow(size(pix_total_unknow,1),3)=pix_actu_unknow(p,3);
                    end
                end
            end
        elseif r>rc && c<cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,1)<taille_c-(toleft) && pix_actu(p,2)>(todown)
                        pix_total(size(pix_total,1)+1,1)=pix_actu(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total(size(pix_total,1),2)=pix_actu(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total(size(pix_total,1),3)=pix_actu(p,3);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,1)<taille_c-(toleft) && pix_actu_gfp(p,2)>(todown)
                        pix_total_gfp(size(pix_total_gfp,1)+1,1)=pix_actu_gfp(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total_gfp(size(pix_total_gfp,1),2)=pix_actu_gfp(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total_gfp(size(pix_total_gfp,1),3)=pix_actu_gfp(p,3);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,1)<taille_c-(toleft) && pix_actu_unknow(p,2)>(todown)
                        pix_total_unknow(size(pix_total_unknow,1)+1,1)=pix_actu_unknow(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total_unknow(size(pix_total_unknow,1),2)=pix_actu_unknow(p,2)+((taille_r*abs(rc-r))-(todown*abs(rc-r)));
                        pix_total_unknow(size(pix_total_unknow,1),3)=pix_actu_unknow(p,3);
                    end
                end
            end
        elseif r<rc && c>cc
            if haveNONGFP
                for p=1:size(pix_actu,1)
                    if pix_actu(p,1)>(toright) && pix_actu(p,2)<taille_r-(toup)
                        pix_total(size(pix_total,1)+1,1)=pix_actu(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total(size(pix_total,1),2)=pix_actu(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total(size(pix_total,1),3)=pix_actu(p,3);
                    end
                end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,1)>(toright) && pix_actu_gfp(p,2)<taille_r-(toup)
                        pix_total_gfp(size(pix_total_gfp,1)+1,1)=pix_actu_gfp(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total_gfp(size(pix_total_gfp,1),2)=pix_actu_gfp(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total_gfp(size(pix_total_gfp,1),3)=pix_actu_gfp(p,3);
                    end
                end
            end
             if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,1)>(toright) && pix_actu_unknow(p,2)<taille_r-(toup)
                        pix_total_unknow(size(pix_total_unknow,1)+1,1)=pix_actu_unknow(p,1)+((taille_c*abs(cc-c))-(toright*abs(cc-c)));
                        pix_total_unknow(size(pix_total_unknow,1),2)=pix_actu_unknow(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total_unknow(size(pix_total_unknow,1),3)=pix_actu_unknow(p,3);
                    end
                end
            end
        elseif r<rc && c<cc
            if haveNONGFP
                 for p=1:size(pix_actu,1)
                    if pix_actu(p,1)<taille_c-(toleft) && pix_actu(p,2)<taille_r-(toup)
                        pix_total(size(pix_total,1)+1,1)=pix_actu(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total(size(pix_total,1),2)=pix_actu(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total(size(pix_total,1),3)=pix_actu(p,3);
                    end
                 end
            end
            if haveGFP
                for p=1:size(pix_actu_gfp,1)
                    if pix_actu_gfp(p,1)<taille_c-(toleft) && pix_actu_gfp(p,2)<taille_r-(toup)
                        pix_total_gfp(size(pix_total,1)+1,1)=pix_actu_gfp(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total_gfp(size(pix_total,1),2)=pix_actu_gfp(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total_gfp(size(pix_total,1),3)=pix_actu_gfp(p,3);
                    end
                end
            end
            if haveUNKNOW
                for p=1:size(pix_actu_unknow,1)
                    if pix_actu_unknow(p,1)<taille_c-(toleft) && pix_actu_unknow(p,2)<taille_r-(toup)
                        pix_total_unknow(size(pix_total_unknow,1)+1,1)=pix_actu_unknow(p,1)-((taille_c*abs(cc-c))-(toleft*abs(cc-c)));
                        pix_total_unknow(size(pix_total_unknow,1),2)=pix_actu_unknow(p,2)-((taille_r*abs(rc-r))-(toup*abs(rc-r)));
                        pix_total_unknow(size(pix_total_unknow,1),3)=pix_actu_unknow(p,3);
                    end
                end
            end
        end

    end
    ind_pos=ind_pos+1;
end
csvwrite(strcat('RT',tranche,'_non_gfp.csv'),pix_total);
csvwrite(strcat('RT',tranche,'_gfp.csv'),pix_total_gfp);
csvwrite(strcat('RT',tranche,'_unknow.csv'),pix_total_unknow);
end

